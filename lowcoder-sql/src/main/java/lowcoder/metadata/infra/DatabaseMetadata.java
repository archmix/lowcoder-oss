package lowcoder.metadata.infra;

import lowcoder.metadata.interfaces.*;
import lowcoder.metadata.interfaces.ForeignKey.Rule;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@RequiredArgsConstructor(staticName = "create")
public class DatabaseMetadata {
  private final Logger logger = LoggerFactory.getLogger(DatabaseMetadata.class);

  private final Connection connection;

  public static enum ObjectType {
    TABLE("TABLE"),
    VIEW("VIEW"),
    SYSTEM_TABLE("SYSTEM TABLE"),
    GLOBAL_TEMPORARY("GLOBAL TEMPORARY"),
    LOCAL_TEMPORARY("LOCAL TEMPORARY"),
    ALIAS("ALIAS"),
    SYNONYM("SYNONYM");

    private String value;

    ObjectType(String value) {
      this.value = value;
    }

    public String value() {
      return this.value;
    }

    public static Optional<ObjectType> of(String code) {
      for (ObjectType type : ObjectType.values()) {
        if (code.equalsIgnoreCase(type.value)) {
          return Optional.of(type);
        }
      }
      return Optional.empty();
    }
  }

  public Tables getTables(DatabaseCatalog dbCatalog, DatabaseSchema dbSchema) {
    Map<String, Table> tables = new HashMap<>();

    try (ResultSet resultSet = this.connection.getMetaData().getTables(dbCatalog.value(), dbSchema.value(), null, new String[]{ObjectType.TABLE.value()});) {
      while (resultSet.next()) {
        final DatabaseCatalog catalog = DatabaseCatalog.create(resultSet.getString("TABLE_CAT"));
        final DatabaseSchema schema = DatabaseSchema.create(resultSet.getString("TABLE_SCHEM"));
        final ObjectType type = ObjectType.of(resultSet.getString("TABLE_TYPE")).orElse(null);
        final String name = resultSet.getString("TABLE_NAME");

        final Table table = Table.create(catalog, schema, Name.create(name), type);
        this.setColumns(table);
        this.setConstraints(table);
        this.setPrimaryKeys(table);
        tables.put(table.getName().value(), table);
      }
    } catch (SQLException e) {
      logger.error("Could not retrieve data", e);
    }

    this.setForeignKeys(tables);

    return new Tables(tables);
  }

  private void setColumns(Table table) {
    Map<String, Column> columns = new HashMap<>();

    try (ResultSet resultSet = this.connection.getMetaData().getColumns(table.getCatalog().value(), table.getSchema().value(), table.getName().value(), null);) {
      while (resultSet.next()) {
        final Column column = Column.create(
          table,
          Name.create(resultSet.getString("COLUMN_NAME")),
          Column.ColumnType.of(resultSet.getInt("DATA_TYPE")).orElse(null),
          resultSet.getLong("COLUMN_SIZE"),
          resultSet.getLong("DECIMAL_DIGITS"),
          "YES".equalsIgnoreCase(resultSet.getString("IS_AUTOINCREMENT")),
          "YES".equalsIgnoreCase(resultSet.getString("IS_GENERATEDCOLUMN"))
        );

        column.add(ConstraintType.NULLABLE, resultSet.getInt("NULLABLE") == 1);
        column.add(ConstraintType.DEFAULT, resultSet.getString("COLUMN_DEF"));

        table.add(column);
      }
    } catch (SQLException e) {
      logger.error("Could not retrieve data", e);
    }
  }

  private void setConstraints(Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getIndexInfo(table.getCatalog().value(), table.getSchema().value(), table.getName().value(), false, false);) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        final String indexName = resultSet.getString("INDEX_NAME");
        final String check = resultSet.getString("FILTER_CONDITION");
        final Boolean unique = !resultSet.getBoolean("NON_UNIQUE");

        ConstraintType type = ConstraintType.INDEX;
        if(unique) {
          type = ConstraintType.UNIQUE;
        } else if (check != null){
          type = ConstraintType.CHECK;
        }

        final ConstraintType constraintType = type;
        table.getColumn(columnName).ifPresent(column -> {
          column.add(constraintType, IndexName.create(indexName));
        });
      }
    } catch (SQLException e) {
      logger.error("Could not retrieve data", e);
    }
  }

  private void setPrimaryKeys(Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getPrimaryKeys(table.getCatalog().value(), table.getSchema().value(), table.getName().value());) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        final IndexName indexName = IndexName.create(resultSet.getString("PK_NAME"));

        table.getColumn(columnName).ifPresent(column -> {
          column.add(ConstraintType.PRIMARY_KEY, indexName);
        });
      }
    } catch (SQLException e) {
      logger.error("Could not retrieve data", e);
    }
  }

  private void setForeignKeys(Map<String, Table> tables) {
    tables.values().forEach(table ->{
      this.setForeignKeys(tables, table);
    });
  }

  private void setForeignKeys(Map<String, Table> tables, Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getImportedKeys(table.getCatalog().value(), table.getSchema().value(), table.getName().value());) {
      while (resultSet.next()) {
        final Name fkColumnName = Name.create(resultSet.getString("FKCOLUMN_NAME"));
        final Name pkColumnName = Name.create(resultSet.getString("PKCOLUMN_NAME"));
        final IndexName fkIndexName = IndexName.create(resultSet.getString("FK_NAME"));
        final Rule onUpdate = Rule.of(resultSet.getInt("UPDATE_RULE"));
        final Rule onDelete = Rule.of(resultSet.getInt("DELETE_RULE"));

        Name pkTableName = Name.create(resultSet.getString("PKTABLE_NAME"));
        final Table pkTable = tables.get(pkTableName.value());
        final Column pkColumn = pkTable.getColumn(pkColumnName.value()).get();

        table.getColumn(fkColumnName.value()).ifPresent(fkColumn -> {
          ForeignKey foreignKey = ForeignKey.create(fkIndexName, pkColumn, onUpdate, onDelete);
          fkColumn.add(ConstraintType.FOREIGN_KEY, foreignKey);
        });
      }
    } catch (SQLException e) {
      logger.error("Could not retrieve data", e);
    }
  }
}
