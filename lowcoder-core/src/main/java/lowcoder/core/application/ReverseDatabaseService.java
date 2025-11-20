package lowcoder.core.application;

import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.LowcoderConfig;
import lowcoder.core.interfaces.RouterService;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.promise.interfaces.PromiseHandler;
import lowcoder.promise.interfaces.Promises;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.infra.TableCache;
import morphos.api.interfaces.MorphosReflector;
import morphos.api.interfaces.Schema;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ServiceLoader;

@Slf4j
public class ReverseDatabaseService {
  private final ConnectionPool pool;

  private ReverseDatabaseService(ConnectionPool pool) {
    this.pool = pool;
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
    var schemaName = LowcoderConfig.Database.getSchema(config(vertx));

    var morphosCache = MorphosReflector.reflect(connection, Schema.of(schemaName));

    Promises promises = Promises.promises();

    morphosCache.tables().forEach(table -> {
      TableCache.of().add(table);
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
    var config = config(vertx);

    try {

      String url = LowcoderConfig.Database.getUrl(config);
      String username = LowcoderConfig.Database.getUser(config);
      String password = LowcoderConfig.Database.getPassword(config);

      handler.handle(Future.succeededFuture(DriverManager.getConnection(url, username, password)));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }

  private JsonObject config(Vertx vertx) {
    return vertx.getOrCreateContext().config();
  }

  private void close(Connection connection){
    try{
      connection.close();
    } catch (Exception e) {
      log.error("Error when trying to close connection", e);
    }
  }
}
