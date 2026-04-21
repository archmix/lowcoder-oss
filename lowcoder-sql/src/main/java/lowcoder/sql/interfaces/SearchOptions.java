package lowcoder.sql.interfaces;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lowcoder.sql.infra.SelectSqlHandler;

import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public class SearchOptions {
  private static final Collector<CharSequence, ?, String> COMMA_JOINING = Collectors.joining(",");
  private final SelectSqlHandler selectSqlHandler;

  public String getSql() {
    return this.selectSqlHandler.getSql();
  }

  public String getSql(PaginationType paginationType) {
    return this.selectSqlHandler.getPagedSql(paginationType);
  }

  public void setValues(PaginationType paginationType, Tuple tuple) {
    this.selectSqlHandler.setValues(paginationType, tuple);
  }

  public void setValues(Tuple tuple){
    this.selectSqlHandler.setValues(tuple);
  }

  public PagedData toPagedData(RowSet<Row> rows) {
    return this.selectSqlHandler.toPagedData(rows);
  }

  public JsonObject toJson(Row row) {
    return this.selectSqlHandler.toJson(row);
  }
}
