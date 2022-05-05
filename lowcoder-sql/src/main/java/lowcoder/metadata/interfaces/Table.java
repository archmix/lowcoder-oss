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
  private final Name name;

  @Getter
  private final DatabaseMetadata.ObjectType type;

  private final Map<String, Column> columns = new HashMap<>();

  public void add(Column column) {
    this.columns.put(column.getName().value(), column);
  }

  public List<Column> find(Predicate<Column> predicate) {
    return stream(predicate).collect(Collectors.toList());
  }

  public Optional<Column> getColumn(String name) {
    return Optional.ofNullable(this.columns.get(name));
  }

  public List<Column> getForeignKeys() {
    return this.find(column -> column.getConstraints().ifConstraint(ConstraintType.FOREIGN_KEY).isPresent());
  }

  public List<Column> getPrimaryKeys() {
    return this.find(column -> column.getConstraints().ifConstraint(ConstraintType.PRIMARY_KEY).isPresent());
  }

  public Stream<Column> stream(Predicate<Column> predicate) {
    return this.columns.values().stream().filter(predicate);
  }
}