package lowcoder.core.application;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.ConfigEntries;
import lowcoder.metadata.infra.TableCache;
import lowcoder.core.interfaces.RouterService;
import lowcoder.metadata.infra.DatabaseMetadata;
import lowcoder.metadata.interfaces.DatabaseCatalog;
import lowcoder.metadata.interfaces.DatabaseSchema;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.promise.interfaces.PromiseHandler;
import lowcoder.promise.interfaces.Promises;
import lowcoder.sql.infra.ConnectionPool;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ServiceLoader;

@Slf4j
public class ReverseDatabaseService {
  private final TableCache tableCache;

  private final ConnectionPool pool;

  private ReverseDatabaseService(ConnectionPool pool) {
    this.pool = pool;
    this.tableCache = TableCache.of();
  }

  public static ReverseDatabaseService create(ConnectionPool pool) {
    return new ReverseDatabaseService(pool);
  }

  public void accept(Vertx vertx, Router router, FuturePromise<Void> futurePromise) {
    openConnection(vertx, handler ->{
      if(handler.succeeded()) {
        Connection connection = handler.result();
        doReverse(vertx, router, connection, futurePromise);
        close(connection);
        return;
      }
      futurePromise.fail(handler.cause());
    });

  }

  private void doReverse(Vertx vertx, Router router, Connection connection, FuturePromise<Void> promise){
    log.info("Reversing database...");
    DatabaseMetadata.create(connection).loadTables(DatabaseCatalog.empty(), DatabaseSchema.create("public"), this.tableCache::add);

    Promises promises = Promises.promises();

    this.tableCache.stream().forEach(table -> {
      ServiceLoader.load(RouterService.class).forEach(service -> {
        log.info("Registering router service {} for table {}", service.getClass().getName(), table.getName());
        service.accept(vertx, router, pool, table, promises.add());
      });
    });

    promises.all().onFailure(promise::fail).onSuccess(result -> {
      promise.complete();
    });
  }

  private void openConnection(Vertx vertx, PromiseHandler<Connection> handler){
    var config = vertx.getOrCreateContext().config();

    try {

      String url = ConfigEntries.Database.getUrl(config);
      String username = ConfigEntries.Database.getUser(config);
      String password = ConfigEntries.Database.getPassword(config);

      handler.handle(Future.succeededFuture(DriverManager.getConnection(url, username, password)));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  private void close(Connection connection){
    try{
      connection.close();
    } catch (Exception e) {
      log.error("Error when trying to close connection", e);
    }
  }
}
