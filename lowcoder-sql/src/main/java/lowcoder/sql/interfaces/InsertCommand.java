package lowcoder.sql.interfaces;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.SqlConnection;
import io.vertx.sqlclient.Transaction;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Column;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.SQLCache;

import java.text.MessageFormat;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class InsertCommand {
  private final SQLCache sqlCache = SQLCache.of();
  private final JDBCPool pool;
  private final Table table;

  public void execute(JsonObject json, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    String sql = this.toSQL(table);
    log.debug("Insert SQL generated for table {} is {}", table.getName(), sql);

    this.pool.preparedQuery(sql).execute(values(json)).onSuccess(rows ->{
      onSuccess.handle(null);
    }).onFailure(onFail);
  }

  public void executeAndGetGeneratedKeys(JsonObject json, Handler<JsonObject> onSuccess, Handler<Throwable> onFail) {
    String sql = toSQL(table);

    this.pool.preparedQuery(sql).execute(values(json)).onSuccess(rows ->{
      Row lastInsertedId = rows.property(JDBCPool.GENERATED_KEYS);
      JsonObject ids = new JsonObject();
      table.getPrimaryKeys().forEach(pk -> {
        ids.put(pk.getColumn().getName(), lastInsertedId.getValue(pk.getColumn().getName()));
      });
      onSuccess.handle(ids);
    }).onFailure(onFail);
  }

  private String toSQL(Table table) {
    var sql = this.sqlCache.get(table, SQLCache.Command.UPDATE);
    if(sql != null) {
      return sql;
    }

    List<String> columnNames = table.getColumns().stream().map(Column::getName).collect(Collectors.toList());
    var collector = Collectors.joining(",");

    String insertTemplate = "INSERT INTO {0}({1}) VALUES ({2})";
    String columnsNames = columnNames.stream().collect(collector);
    String columnsValues = columnNames.stream().map(column -> "?").collect(collector);

    String generatedSQL = MessageFormat.format(insertTemplate, table.getName(), columnsNames, columnsValues);
    this.sqlCache.add(table, SQLCache.Command.INSERT, generatedSQL);

    return generatedSQL;
  }

  private Tuple values(JsonObject json) {
    List<Object> values = this.table.getColumns().stream().map(column -> json.getValue(column.getName())).collect(Collectors.toList());
    return Tuple.tuple(values);
  }
}
