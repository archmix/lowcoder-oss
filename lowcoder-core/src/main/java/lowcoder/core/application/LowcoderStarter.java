package lowcoder.core.application;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.application.AbstractContainerService;
import lowcoder.api.interfaces.StartupService;
import lowcoder.api.interfaces.ContainerServiceSpecification;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.promise.interfaces.Promises;
import lowcoder.sql.infra.ConnectionPool;
import org.slf4j.MDC;

import java.util.ServiceLoader;
import java.util.UUID;

@ContainerServiceSpecification
@Slf4j
public class LowcoderStarter extends AbstractContainerService {
  private ConnectionPool pool;
  public static final String X_REQUEST_ID = "X-Request-ID";

  @Override
  protected void init() {
    this.pool = ConnectionPool.create(vertx, config());
  }

  @Override
  public void stop(FuturePromise<Void> stopPromise) {
    log.info("Lowcoder Container stopping...");
    this.pool.close();
    stopPromise.complete();
  }

  @Override
  public void start(FuturePromise<Void> startPromise) {
    log.info("Lowcoder Container starting...");
    log.info("Working directory is {} ", System.getProperty("user.dir"));

    var router = Router.router(vertx);

    router.route().handler(ctx -> {
      String requestId = ctx.request().getHeader("X-Request-ID");
      if (requestId == null || requestId.isEmpty()) {
        requestId = UUID.randomUUID().toString();
      }
      ctx.put(X_REQUEST_ID, requestId);
      MDC.put(X_REQUEST_ID, requestId);

      ctx.addBodyEndHandler(v -> {
        MDC.clear();
      });

      ctx.next();
    });

    router.route("/").handler(ctx -> {
      HttpServerResponse response = ctx.response();
      response.putHeader("content-type", "text/plain");
      response.end("Hello from Lowcoder OSS");
    });

    var promises = Promises.promises();

    ReverseDatabaseService.create(pool).accept(vertx, router, promises.add());

    ServiceLoader.load(StartupService.class).forEach(service -> {
      service.accept(vertx, router, promises.add());
    });

    promises.all().onFailure(startPromise::fail).onSuccess(result -> {
      startPromise.complete();
    });
  }
}
