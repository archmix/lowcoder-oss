package lowcoder.core.application;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.application.AbstractContainerService;
import lowcoder.api.interfaces.ContainerServiceSpecification;
import lowcoder.api.interfaces.StartupService;
import lowcoder.api.interfaces.StartupServiceSpecification;
import lowcoder.promise.interfaces.Handlers;
import lowcoder.promise.interfaces.FuturePromise;

@ContainerServiceSpecification
@StartupServiceSpecification
@Slf4j
public class HttpServerService extends AbstractContainerService implements StartupService {
  private static HttpServer server;

  public void accept(Vertx vertx, Router router, FuturePromise<Void> startPromise) {
    int httpPort = vertx.getOrCreateContext().config().getNumber("HTTP_PORT", 8080).intValue();
    server.requestHandler(router).listen(httpPort, result -> {
      if (result.failed()) {
        log.error("HTTP Server failed to start", result.cause());
        startPromise.fail(result.cause());
        return;
      }
      startPromise.complete();
      log.info("HTTP Server is accepting connections on {}", httpPort);
    });
  }

  @Override
  public void start(FuturePromise<Void> startPromise) {
    server = vertx.createHttpServer();
    server.exceptionHandler(Handlers.exceptionHandler());
  }

  @Override
  public void stop(FuturePromise<Void> stopPromise) {
    server.close();
  }
}
