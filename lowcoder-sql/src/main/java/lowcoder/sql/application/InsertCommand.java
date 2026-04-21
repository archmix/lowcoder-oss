package lowcoder.sql.application;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.sql.infra.InsertSqlHandler;

import static lowcoder.promise.interfaces.Handlers.*;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class InsertCommand {
  private final JDBCPool pool;
  private final InsertSqlHandler handler;

  public void execute(JsonObject json, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    var sql = handler.getSql();
    var values = handler.values(json);

    this.pool.preparedQuery(sql).execute(values).onSuccess(rows -> {
      onSuccess.handle(null);
    }).onFailure(onFail);
  }

  public void executeAndGetGeneratedKeys(JsonObject json, Handler<JsonObject> onSuccess, Handler<Throwable> onFail) {
    try {
      var sql = this.handler.getSql();
      var values = this.handler.values(json);

      this.pool.preparedQuery(sql).execute(values).onSuccess(rows -> {
        tryAndCatch(() -> handleGeneratedKeys(onSuccess, rows), onFail);
      }).onFailure(onFail);
    } catch (Throwable e) {
      onFail.handle(e);
    }
  }

  private void handleGeneratedKeys(Handler<JsonObject> onSuccess, RowSet<Row> rows) {
    Row generatedIds = rows.property(JDBCPool.GENERATED_KEYS);
    var ids = this.handler.getGeneratedKeys(generatedIds);
    onSuccess.handle(ids);
  }
}
