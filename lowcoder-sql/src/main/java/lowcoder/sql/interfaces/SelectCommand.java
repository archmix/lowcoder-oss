package lowcoder.sql.interfaces;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.sql.infra.PaginationType;

import java.rmi.NoSuchObjectException;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class SelectCommand {
  private final JDBCPool pool;
  private final PaginationType paginationType;

  public final void findOne(SearchOptions options, Handler<JsonObject> onSuccess, Handler<Throwable> onFail) {
    var sql = options.getSQL();

    var tuple = Tuple.tuple();
    options.setValues(tuple);

    pool.preparedQuery(sql).execute(tuple).onSuccess(rows ->{
      if(rows.size() == 0) {
        handleNoData(onFail);
        return;
      }

      if(rows.size() == 1) {
        onSuccess.handle(options.toJson(rows));
        return;
      }

      var error = new IllegalStateException("Expected one row, but got " + rows.size());
      handleError(onFail, error);
    }).onFailure(onFail);
  }

  public final void findPaged(SearchOptions options, Handler<PagedData> onSuccess, Handler<Throwable> onFail) {
    var sql = options.getSQL(paginationType);

    var tuple = Tuple.tuple();
    options.setValues(paginationType, tuple);

    pool.preparedQuery(sql).execute(tuple).onSuccess(rows ->{
      if(rows.size() == 0) {
        handleNoData(onFail);
        return;
      }

      onSuccess.handle(options.toPagedData(rows));
    }).onFailure(onFail);
  }

  private void handleNoData(Handler<Throwable> onFail) {
    var error = new NoSuchObjectException("No data found for provided query");
    handleError(onFail, error);
  }

  private void handleError(Handler<Throwable> onFail, Throwable error) {
    log.error(error.getMessage(), error);
    onFail.handle(error);
  }
}
