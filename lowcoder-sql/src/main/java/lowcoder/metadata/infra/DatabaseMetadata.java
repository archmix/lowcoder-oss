package lowcoder.metadata.infra;

import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.*;
import lowcoder.metadata.interfaces.ForeignKey.Rule;
import lombok.RequiredArgsConstructor;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Consumer;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class DatabaseMetadata {
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

  public void loadTables(DatabaseCatalog dbCatalog, DatabaseSchema dbSchema, Consumer<Table> consumer) {
    Map<String, Table> tables = new HashMap<>();

    try (ResultSet resultSet = this.connection.getMetaData().getTables(dbCatalog.value(), dbSchema.value(), "%", new String[]{ObjectType.TABLE.value()});) {
      while (resultSet.next()) {
        final DatabaseCatalog catalog = DatabaseCatalog.create(resultSet.getString("TABLE_CAT"));
        final DatabaseSchema schema = DatabaseSchema.create(resultSet.getString("TABLE_SCHEM"));
        final ObjectType type = ObjectType.of(resultSet.getString("TABLE_TYPE")).orElse(null);
        final String name = resultSet.getString("TABLE_NAME");

        final Table table = Table.create(catalog, schema, name, type);
        this.setColumns(table);
        this.setConstraints(table);
        this.setPrimaryKeys(table);
        this.setForeignKeys(table);
        consumer.accept(table);
      }
    } catch (SQLException e) {
      log.error("Could not retrieve data", e);
      throw new RuntimeException(e);
    }
  }

  private void setColumns(Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getColumns(table.getCatalog().value(), table.getSchema().value(), table.getName(), null);) {
      while (resultSet.next()) {
        final Column column = Column.create(
          table,
          resultSet.getString("COLUMN_NAME"),
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
      log.error("Could not retrieve data", e);
      throw new RuntimeException(e);
    }
  }

  private void setConstraints(Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getIndexInfo(table.getCatalog().value(), table.getSchema().value(), table.getName(), false, false);) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        final String indexName = resultSet.getString("INDEX_NAME");
        final String check = resultSet.getString("FILTER_CONDITION");
        final boolean unique = !resultSet.getBoolean("NON_UNIQUE");

        ConstraintType type = ConstraintType.INDEX;
        if(unique) {
          type = ConstraintType.UNIQUE;
        } else if (check != null){
          type = ConstraintType.CHECK;
        }

        final ConstraintType constraintType = type;
        table.getColumn(columnName).ifPresent(column -> {
          column.add(constraintType, indexName);
        });
      }
    } catch (SQLException e) {
      log.error("Could not retrieve data", e);
      throw new RuntimeException(e);
    }
  }

  private void setPrimaryKeys(Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getPrimaryKeys(table.getCatalog().value(), table.getSchema().value(), table.getName());) {
      while (resultSet.next()) {
        final String columnName = resultSet.getString("COLUMN_NAME");
        final String indexName = resultSet.getString("PK_NAME");

        PrimaryKey primaryKey = PrimaryKey.create(indexName, table.getColumn(columnName).get());
        table.add(primaryKey);
      }
    } catch (SQLException e) {
      log.error("Could not retrieve data", e);
      throw new RuntimeException(e);
    }
  }

  private void setForeignKeys(Table table) {
    try (ResultSet resultSet = this.connection.getMetaData().getImportedKeys(table.getCatalog().value(), table.getSchema().value(), table.getName());) {
      while (resultSet.next()) {
        final String fkColumnName = resultSet.getString("FKCOLUMN_NAME");
        final String fkIndexName = resultSet.getString("FK_NAME");
        final Rule onUpdate = Rule.of(resultSet.getInt("UPDATE_RULE"));
        final Rule onDelete = Rule.of(resultSet.getInt("DELETE_RULE"));

        String pkTableName = resultSet.getString("PKTABLE_NAME");

        table.getColumn(fkColumnName).ifPresent(fkColumn -> {
          ForeignKey foreignKey = ForeignKey.create(fkIndexName, pkTableName, fkColumn, onUpdate, onDelete);
          table.add(foreignKey);
        });
      }
    } catch (SQLException e) {
      log.error("Could not retrieve data", e);
      throw new RuntimeException(e);
    }
  }
}
