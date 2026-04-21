package lowcoder.sql.application;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.RowSet;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.sql.interfaces.PagedData;
import lowcoder.sql.interfaces.PaginationType;
import lowcoder.sql.interfaces.SearchOptions;

import java.rmi.NoSuchObjectException;

import static lowcoder.promise.interfaces.Handlers.*;

@Slf4j
@RequiredArgsConstructor(staticName = "create")
public class SelectCommand {
  private final JDBCPool pool;
  private final PaginationType paginationType;

  public final void findOne(SearchOptions options, Handler<JsonObject> onSuccess, Handler<Throwable> onFail) {
    tryAndCatch(() ->{
      var sql = options.getSql();

      var tuple = Tuple.tuple();
      options.setValues(tuple);

      pool.preparedQuery(sql).execute(tuple).onSuccess(rows ->{
        tryAndCatch(() -> handleOneRow(options, onSuccess, onFail, rows), onFail);
      }).onFailure(onFail);
    }, onFail);
  }

  public final void findPaged(SearchOptions options, Handler<PagedData> onSuccess, Handler<Throwable> onFail) {
    var sql = options.getSql(paginationType);

    var tuple = Tuple.tuple();
    options.setValues(paginationType, tuple);

    pool.preparedQuery(sql).execute(tuple).onSuccess(rows ->{
      tryAndCatch(() -> handlePagedRows(options, onSuccess, onFail, rows), onFail);
    }).onFailure(onFail);
  }

  private void handlePagedRows(SearchOptions options, Handler<PagedData> onSuccess, Handler<Throwable> onFail, RowSet<Row> rows) {
    if(rows.size() == 0) {
      handleNoData(onFail);
      return;
    }

    onSuccess.handle(options.toPagedData(rows));
  }

  private void handleOneRow(SearchOptions options, Handler<JsonObject> onSuccess, Handler<Throwable> onFail, RowSet<Row> rows) {
    if (rows.size() == 0) {
      handleNoData(onFail);
      return;
    }

    if (rows.size() == 1) {
      onSuccess.handle(options.toJson(rows.iterator().next()));
      return;
    }

    var error = new IllegalStateException("Expected one row, but got " + rows.size());
    handleError(onFail, error);
  }

  private void handleNoData(Handler<Throwable> onFail) {
    var error = new NoSuchObjectException("No data found for provided query and data set");
    handleError(onFail, error);
  }

  private void handleError(Handler<Throwable> onFail, Throwable error) {
    log.error(error.getMessage(), error);
    onFail.handle(error);
  }
}
