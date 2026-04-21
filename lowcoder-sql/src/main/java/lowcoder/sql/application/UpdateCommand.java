package lowcoder.sql.application;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.sql.infra.UpdateSqlHandler;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class UpdateCommand {
  private final JDBCPool pool;
  private final UpdateSqlHandler handler;

  public void execute(JsonObject json, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    var sql = handler.getSql(json.fieldNames());

    var values = handler.values(json);

    this.pool.preparedQuery(sql).execute(values).onSuccess(rows ->{
      if(rows.rowCount() <= 0) {
        onFail.handle(new RuntimeException("Expected 1 row to be updated, but got " + rows.size()));
        return;
      }
      onSuccess.handle(null);
    }).onFailure(onFail);
  }
}
