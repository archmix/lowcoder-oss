package lowcoder.core.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Future;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lowcoder.core.interfaces.RouterService;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.metadata.infra.DatabaseMetadata;
import lowcoder.metadata.interfaces.DatabaseCatalog;
import lowcoder.metadata.interfaces.DatabaseSchema;
import lowcoder.promise.interfaces.AsyncHandlers;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.promise.interfaces.PromiseHandler;
import lowcoder.promise.interfaces.Promises;
import lowcoder.api.infra.ConfigEntries;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.ServiceLoader;

public class LowcoderContainer extends AbstractVerticle {
  private static final Logger LOGGER = LoggerFactory.getLogger(LowcoderContainer.class);

  private ConnectionPool pool;

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    this.pool = ConnectionPool.create(vertx, this.config());
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    LOGGER.info("Lowcoder Container starting...");
    LOGGER.info("Working directory is {} ", System.getProperty("user.dir"));

    Router router = Router.router(vertx);
    router.route("/").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello from Lowcoder OSS");
    });

    Promises promises = Promises.promises();
    startReverseDatabase(promises.add(), router);
    startHttp(promises.add(), router);

    promises.all().onFailure(startPromise::fail).onSuccess(result -> {
      startPromise.complete();
    });
  }

  private void startReverseDatabase(FuturePromise<Void> startPromise, Router router) {
    openConnection(handler ->{
        if(handler.succeeded()) {
          Connection connection = handler.result();
          doReverse(startPromise, router, connection);
          close(connection);
          startPromise.complete();
          return;
        }
        startPromise.fail(handler.cause());
    });
  }

  private void doReverse(FuturePromise<Void> startPromise, Router router, Connection connection){
    LOGGER.info("Reversing database...");
    DatabaseMetadata.create(connection).loadTables(DatabaseCatalog.empty(), DatabaseSchema.create("public"), table -> {
      ServiceLoader.load(RouterService.class).forEach(service -> {
        LOGGER.info("Registering router service {} for table {}", service.getClass().getName(), table.getName());
        service.accept(vertx, router, pool, table);
      });
    });
  }

  private void startHttp(FuturePromise<Void> startPromise, Router router) {
    HttpServer server = vertx.createHttpServer();
    server.exceptionHandler(AsyncHandlers.exceptionHandler());

    int httpPort = config().getNumber("HTTP_PORT", 8080).intValue();

    server.requestHandler(router).listen(httpPort, result -> {
      if (result.failed()) {
        LOGGER.error("HTTP Server failed to start", result.cause());
        startPromise.fail(result.cause());
        return;
      }
      startPromise.complete();
      LOGGER.info("HTTP Server is accepting connections on {}", httpPort);
    });
  }

  @Override
  public void stop() throws Exception {
    this.pool.close();
  }

  private void close(Connection connection){
    try{
      connection.close();
    } catch (Exception e) {
      LOGGER.error("Error when trying to close connection", e);
    }
  }

  private void openConnection(PromiseHandler<Connection> handler){
    try {

      String url = ConfigEntries.Database.getUrl(config());
      String username = ConfigEntries.Database.getUser(config());
      String password = ConfigEntries.Database.getPassword(config());

      handler.handle(Future.succeededFuture(DriverManager.getConnection(url, username, password)));
    } catch (Exception e) {
      handler.handle(Future.failedFuture(e));
    }
  }
}
