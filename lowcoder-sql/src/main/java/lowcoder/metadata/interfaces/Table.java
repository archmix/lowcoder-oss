package lowcoder.metadata.interfaces;

import lowcoder.metadata.infra.DatabaseMetadata;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(staticName = "create")
public class Table {
  @Getter
  private final DatabaseCatalog catalog;

  @Getter
  private final DatabaseSchema schema;

  @Getter
  private final String name;

  @Getter
  private final DatabaseMetadata.ObjectType type;

  private final Map<String, Column> columns = new HashMap<>();

  private final Map<String, ForeignKey> foreignKeys = new HashMap<>();

  private final Map<String, PrimaryKey> primaryKeys = new HashMap<>();

  public void add(Column column) {
    this.columns.put(column.getName(), column);
  }

  public void add(ForeignKey foreignKey) {
    this.columns.remove(foreignKey.getColumn().getName());
    this.foreignKeys.put(foreignKey.getColumn().getName(), foreignKey);
  }

  public void add(PrimaryKey primaryKey) {
    this.columns.remove(primaryKey.getColumn().getName());
    this.primaryKeys.put(primaryKey.getColumn().getName(), primaryKey);
  }

  public Collection<Column> find(Predicate<Column> predicate) {
    return this.columns.values().stream().filter(predicate).collect(Collectors.toList());
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
}