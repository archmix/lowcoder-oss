package lowcoder.sql.interfaces;

import io.vertx.core.json.Json;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lowcoder.metadata.infra.TableCache;
import lowcoder.metadata.interfaces.Column;
import lowcoder.metadata.interfaces.ForeignKey;
import lowcoder.metadata.interfaces.PrimaryKey;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.PaginationType;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static java.text.MessageFormat.*;

@RequiredArgsConstructor(staticName = "create")
public class SearchOptions {
  private final Collection<Column> columns = new ArrayList<>();
  private final Collection<Table> expand = new ArrayList<>();
  private Optional<SortOptions> sort = Optional.empty();
  private final PaginationOptions pagination = new PaginationOptions();
  private final FilterOptions filter = FilterOptions.create();
  private final Table table;

  enum SearchParameters {
    FIELDS("fields"),
    EXPAND("expand"),
    LIMIT("limit"),
    OFFSET("offset"),
    SORT("sort"),
    OR("_or");

    private final String value;

    SearchParameters(String value){
      this.value = value;
    }

    public String value() {
      return this.value;
    }
  }

  public void from(RoutingContext context) {
    var params = context.request().params();
    setColumns(params.getAll(SearchParameters.FIELDS.value()));
    setExpand(params.get(SearchParameters.EXPAND.value()));

    this.table.getPrimaryKeys().stream().findFirst().ifPresent(pk -> {
      String name = pk.getColumn().getName();
      this.sort = Optional.of(SortOptions.of(name));
    });

    for(String key : params.names()) {
      if(key.equals("expand")) {
        continue;
      }
      if(key.equals("limit")) {
        this.pagination.setLimit(Integer.parseInt(params.get(key)));
        continue;
      }
      if(key.equals("offset")) {
        this.pagination.setOffset(Integer.parseInt(params.get(key)));
        continue;
      }
      if(key.equals("sort")) {
        this.sort = Optional.of(SortOptions.of(params.get(key)));
        continue;
      }
      if(key.equals("_or")) {
        continue;
      }

      this.addFilter(key, params.getAll(key), this.filter.and());
    }

    var orParam = params.get("_or");
    if(orParam != null) {
      for(String param : List.of(orParam.split(","))){
        if(param.contains(":")) {
          var fieldValue = param.split(":");
          this.addFilter(fieldValue[0], List.of(fieldValue[1]), this.filter.or());
          continue;
        }
        this.addFilter(param, null, this.filter.or());
      }
    }

    table.getPrimaryKeys().forEach(pk -> {
      String name = pk.getColumn().getName();
      String value = context.pathParam(name);
      if(value != null) {
        this.filter.and().add(pk.getColumn(), Predicate.EQUALS, value);
      }
    });
  }

  private void setExpand(String expand) {
    if(Objects.requireNonNullElse(expand, "").isBlank()) {
      return;
    }

    var entities = expand.split(",");
    for(String entity : entities) {
      if(entity.contains(".")) {
        for(var name : entity.split("\\.")){
          this.expand.add(TableCache.of().get(name));
        }
        continue;
      }

      this.expand.add(TableCache.of().get(entity));
    }
  }

  private void addFilter(String field, Collection<String> values, Criteria criteria) {
    var namedFilter = NamedFilter.create(field);

    if(namedFilter.getPredicate() == Predicate.IN) {
      var inValues = values.stream().filter(value -> value.contains(",")).map(value -> value.split(","))
        .flatMap(Stream::of).collect(Collectors.toList());

      this.addFilter(namedFilter, inValues, criteria);
      return;
    }

    var value = values.stream().findFirst().orElse(null);
    this.addFilter(namedFilter, value, criteria);
  }

  private void addFilter(NamedFilter namedFilter, Object value, Criteria criteria) {
    table.getColumn(namedFilter.getName())
      .ifPresent(column -> criteria.add(column, namedFilter.getPredicate(), value));
  }

  private void setColumns(Collection<String> columns) {
    this.columns.clear();
    if(columns.isEmpty()) {
      this.columns.addAll(table.getColumns());
      this.columns.addAll(table.getForeignKeys().stream().map(ForeignKey::getColumn).collect(Collectors.toList()));
      this.columns.addAll(table.getPrimaryKeys().stream().map(PrimaryKey::getColumn).collect(Collectors.toList()));
      return;
    }
    columns.forEach(field -> {
      var column = table.getColumn(field);
      column.ifPresent(this.columns::add);
    });
  }

  public String getSQL() {
    return this.getSQL("SELECT {0} FROM {1}");
  }

  public String getSQL(PaginationType paginationType) {
    var selectTemplate = "SELECT COUNT(*) OVER() as total, {0} FROM {1}";

    var sql = new StringBuilder(this.getSQL(selectTemplate));
    this.sort.ifPresent(sorting -> sql.append(sorting.getSQL()));
    sql.append(paginationType.getStatement());

    return sql.toString();
  }

  private String getSQL(String selectTemplate){
    var sql = new StringBuilder();
    sql.append(format(selectTemplate, columnNames(), table.getName()));
    sql.append(this.filter.getSQL());

    return sql.toString();
  }

  public void setValues(PaginationType paginationType, Tuple tuple) {
    this.setValues(tuple);
    paginationType.setTuple(tuple, this.pagination.getOffset(), this.pagination.getLimit());
  }

  public void setValues(Tuple tuple){
    this.filter.setValues(tuple);
  }

  public PagedData toPagedData(RowSet<Row> rows) {
    var total = rows.iterator().next().getInteger("total");
    var data = new JsonArray();

    rows.forEach(row -> {
      data.add(this.toJson(row));
    });

    return PagedData.of(total, this.pagination.getLimit(), this.pagination.getOffset(), data);
  }

  public JsonObject toJson(RowSet<Row> rows) {
    var row = rows.iterator().next();
    return this.toJson(row);
  }

  private String columnNames() {
    return columns.stream().map(Column::getName).collect(Collectors.joining(","));
  }

  private JsonObject toJson(Row row) {
    var json = new JsonObject();
    columns.forEach(column -> {
      json.put(column.getName(), row.getValue(column.getName()));
    });

    return json;
  }
}
