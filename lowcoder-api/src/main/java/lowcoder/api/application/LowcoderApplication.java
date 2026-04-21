package lowcoder.api.application;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.LowcoderConfiguration;

import static lombok.AccessLevel.*;
import static lowcoder.promise.interfaces.Handlers.*;

@Slf4j
public class LowcoderApplication {
  public static void start(LowcoderConfiguration configuration, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    Vertx vertx = Vertx.vertx();

    var failedHandler = new VertxFailedHandler(vertx, onFail);

    tryAndCatch(() ->{
      Deployer.create(configuration).deploy(vertx, handler -> {
        log.info("Lowcoder container started");
      }, failedHandler);
    }, failedHandler);
  }

  @RequiredArgsConstructor(access = PRIVATE)
  public static class Deployer {
    private final JsonObject config;

    public static Deployer create(LowcoderConfiguration configuration) {
      return new Deployer(configuration.toJson());
    }

    public void deploy(Vertx vertx, Handler<Void> onComplete, Handler<Throwable> onFail) {
      vertx.deployVerticle(new LowcoderContainer(), new DeploymentOptions().setConfig(config), handler -> {
        if (handler.succeeded()) {
          onComplete.handle(null);
          return;
        }
        onFail.handle(handler.cause());
      });
    }
  }

  @RequiredArgsConstructor(access = PRIVATE)
  @Slf4j
  static class VertxFailedHandler implements Handler<Throwable> {
    private final Vertx vertx;
    private final Handler<Throwable> onFail;

    @Override
    public void handle(Throwable throwable) {
      log.error("Failed to start lowcoder", throwable);
      vertx.close();
      onFail.handle(throwable);
    }
  }
}
