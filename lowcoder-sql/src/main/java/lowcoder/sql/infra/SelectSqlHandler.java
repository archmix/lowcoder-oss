package lowcoder.sql.infra;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.sql.interfaces.Alias;
import lowcoder.sql.interfaces.FieldAliases;
import lowcoder.sql.interfaces.ExpandedFields;
import lowcoder.sql.interfaces.Fields;
import lowcoder.sql.interfaces.FilterOptions;
import lowcoder.sql.interfaces.PagedData;
import lowcoder.sql.interfaces.PaginationOptions;
import lowcoder.sql.interfaces.PaginationType;
import lowcoder.sql.interfaces.SortOptions;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.Table;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static java.text.MessageFormat.*;

@RequiredArgsConstructor(access = lombok.AccessLevel.PRIVATE)
@Slf4j
public class SelectSqlHandler {
  private static final Collector<CharSequence, ?, String> COMMA_JOINING = Collectors.joining(", ");

  private final String tableName;
  private final FieldAliases aliases;
  private final Fields returnFields;
  private final Collection<ExpandedRelation> relations;
  private final FilterOptions filter;
  private final SortOptions sort;
  private final PaginationOptions pagination;

  public static SelectSqlHandler of(Table table) {
    var fields = Fields.create(table, Collections.emptyList());
    var expanded = ExpandedFields.create(table, "");
    var filter = FilterOptions.create();
    var pagination = PaginationOptions.create();

    return of(table, fields, expanded, filter, null, pagination);
  }

  public static SelectSqlHandler of(Table table, Fields fields, ExpandedFields expand, FilterOptions filter,
                                    SortOptions sort, PaginationOptions pagination) {
    var relations = new HashSet<ExpandedRelation>();
    expand.forEach(foreignKey -> relations.add(new ExpandedRelation(foreignKey)));

    var aliases = FieldAliases.create(table.getFields());
    return new SelectSqlHandler(table.getName(), aliases, fields, relations, filter, sort, pagination);
  }

  public String getSql() {
    var sqlTemplate = "SELECT {0} FROM {1}";
    return this.getSql(sqlTemplate);
  }

  public String getPagedSql(PaginationType paginationType) {
    var sqlTemplate = "SELECT COUNT(*) OVER() as total, {0} FROM {1}";
    return this.getSql(sqlTemplate) + paginationType.getStatement();
  }

  public PagedData toPagedData(RowSet<Row> rows) {
    var total = rows.iterator().next().getLong("total");
    var data = new JsonArray();

    rows.forEach(row -> {
      data.add(this.toJson(row));
    });

    return PagedData.create(total, pagination.getLimit(), pagination.getOffset(), data);
  }

  public JsonObject toJson(Row row) {
    var json = new JsonObject();

    this.returnFields.forEach(field -> {
      var alias = this.aliases.getAlias(field);
      json.put(field.getName(), row.getValue(alias.getColumnName()));
    });

    this.relations.forEach(relation -> {
      relation.expand(row, json);
    });

    return json;
  }

  public void setValues(PaginationType paginationType, Tuple tuple) {
    this.setValues(tuple);
    paginationType.setTuple(tuple, this.pagination.getOffset(), this.pagination.getLimit());
  }

  public void setValues(Tuple tuple) {
    this.filter.setValues(this.aliases, tuple);
  }

  private String getSql(String selectTemplate) {
    var columnNames = getSqlColumnNames();

    var fromSql = new StringBuilder();
    fromSql.append(this.tableName);

    for (ExpandedRelation relation : this.relations) {
      fromSql.append(relation.getJoinSql());
    }

    var sql = new StringBuilder();
    sql.append(format(selectTemplate, columnNames, fromSql));
    sql.append(filter.getSql(this.aliases));
    Optional.ofNullable(this.sort).ifPresent(sort -> sql.append(sort.getSql(this.aliases)));

    log.debug("Generated select is: {}", sql);

    return sql.toString();
  }

  private String getSqlColumnNames() {
    var columnNames = new HashSet<String>();
    this.aliases.stream().filter(alias -> this.returnFields.contains(alias.getField())).map(Alias::getAlias).forEach(columnNames::add);

    for (ExpandedRelation relation : this.relations) {
      relation.getAliases().forEach(alias -> columnNames.add(alias.getAlias()));
    }

    return columnNames.stream().collect(COMMA_JOINING);
  }

  @EqualsAndHashCode
  static class ExpandedRelation {
    private final ForeignKey foreignKey;
    private final FieldAliases aliases;

    ExpandedRelation(ForeignKey foreignKey) {
      this.foreignKey = foreignKey;
      Table parent = this.foreignKey.getReferencedTable();
      this.aliases = FieldAliases.create(parent.getFields(), foreignKey.getName()) ;
    }

    Iterable<Alias> getAliases() {
      return this.aliases;
    }

    public String getJoinSql() {
      var leftJoinTemplate = " LEFT JOIN {0} ON {1} = {2}";
      var parentName = this.foreignKey.getReferencedTable().getName() + " " + this.foreignKey.getName();
      var pkName = this.aliases.getAlias(this.foreignKey.getReferencedPrimaryKey()).getFullName();
      var fkName = this.foreignKey.getFullName();

      return format(leftJoinTemplate, parentName, fkName, pkName);
    }

    void expand(Row row, JsonObject parent) {
      var json = new JsonObject();

      this.aliases.forEach((field, alias) ->{
        json.put(field.getName(), row.getValue(alias.getColumnName()));
      });

      parent.put(this.foreignKey.getName(), json);
    }
  }
}
