package lowcoder.metadata.interfaces;

import io.vertx.core.shareddata.Shareable;
import lombok.Builder;
import lowcoder.metadata.infra.DatabaseMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
@Builder
public class Table implements Shareable {
  @Getter
  private final DatabaseCatalog catalog;

  @Getter
  private final DatabaseSchema schema;

  @Getter
  private final String name;

  @Getter
  private final DatabaseMetadata.ObjectType type;

  private final Map<String, Field> fields = new HashMap<>();

  private final Map<String, Column> columns = new HashMap<>();

  private final Map<String, ForeignKey> foreignKeys = new HashMap<>();

  private final Map<String, PrimaryKey> primaryKeys = new HashMap<>();

  public void add(Column column) {
    this.columns.put(column.getName(), column);
    this.addField(column);
  }

  public void add(ForeignKey foreignKey) {
    this.columns.remove(foreignKey.getColumn().getName());
    this.foreignKeys.put(foreignKey.getColumn().getName(), foreignKey);
    this.addField(foreignKey);
  }

  public void add(PrimaryKey primaryKey) {
    this.columns.remove(primaryKey.getColumn().getName());
    this.primaryKeys.put(primaryKey.getColumn().getName(), primaryKey);
    this.addField(primaryKey);
  }

  public Collection<Field> find(Predicate<Field> predicate) {
    return this.fields.values().stream().filter(predicate).collect(Collectors.toList());
  }

  public Optional<Column> getColumn(String name) {
    return Optional.ofNullable(this.columns.get(name));
  }

  public Optional<ForeignKey> getForeignKey(String name) {
    return Optional.ofNullable(this.foreignKeys.get(name));
  }

  public Optional<PrimaryKey> getPrimaryKey(String name) {
    return Optional.ofNullable(this.primaryKeys.get(name));
  }

  public Collection<ForeignKey> getForeignKeys() {
    return this.foreignKeys.values();
  }

  public Collection<PrimaryKey> getPrimaryKeys() {
    return this.primaryKeys.values();
  }

  public Collection<Column> getColumns() {
    return this.columns.values();
  }

  private void addField(Field field) {
    this.fields.put(field.getName(), field);
  }

  public static class TableBuilder {
    private final Collection<Column> columns = new ArrayList<>();
    private final Collection<PrimaryKey> primaryKeys = new ArrayList<>();
    private final Collection<ForeignKey> foreignKeys = new ArrayList<>();

    public Column.ColumnBuilder withColumn(String name) {
      return Column.builder().name(name).root(this);
    }

    TableBuilder add(Column column) {
      this.columns.add(column);
      return this;
    }

    TableBuilder add(PrimaryKey primaryKey) {
      this.primaryKeys.add(primaryKey);
      return this;
    }

    TableBuilder add(ForeignKey foreignKey) {
      this.foreignKeys.add(foreignKey);
      return this;
    }

    public Table build(){
      var table = new Table(this.catalog, this.schema, this.name, this.type);
      this.columns.forEach(table::add);
      this.primaryKeys.forEach(table::add);
      this.foreignKeys.forEach(table::add);
      return table;
    }
  }
}