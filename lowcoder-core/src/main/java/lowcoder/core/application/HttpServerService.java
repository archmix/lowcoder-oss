package lowcoder.core.application;

import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServer;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.interfaces.StartupService;
import lowcoder.api.interfaces.StartupServiceSpecification;
import lowcoder.promise.interfaces.AsyncHandlers;
import lowcoder.promise.interfaces.FuturePromise;

@StartupServiceSpecification
@Slf4j
public class HttpServerService implements StartupService {
  @Override
  public void accept(Vertx vertx, Router router, FuturePromise<Void> startPromise) {
    HttpServer server = vertx.createHttpServer();
    server.exceptionHandler(AsyncHandlers.exceptionHandler());

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
}
