package lowcoder.api.application;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import legolas.provided.infra.LegolasExtension;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.LowcoderConfig;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, LegolasExtension.class})
@Slf4j
public class LowcoderTest {
  protected static JsonObject config = new JsonObject();

  protected static void startLowcoder(Vertx vertx) {
    startLowcoder(vertx, handler -> {});
  }

  protected static void startLowcoder(Vertx vertx, Handler<Void> onComplete) {
    Integer httpPort = LowcoderConfig.HTTP.getPortAvailable();
    config.put(LowcoderConfig.HTTP.PORT, httpPort);

    vertx.deployVerticle(new LowcoderContainer(), new DeploymentOptions().setConfig(config), handler -> {
      if (handler.succeeded()) {
        onComplete.handle(null);
        return;
      }
      log.error("Failed to start lowcoder", handler.cause());
    });
  }

  protected static void setDatabaseConfig(String schema, String url, String driver, String username, String password) {
    config.put(LowcoderConfig.Database.SCHEMA, schema);
    config.put(LowcoderConfig.Database.URL, url);
    config.put(LowcoderConfig.Database.DRIVER, driver);
    config.put(LowcoderConfig.Database.USER, username);
    config.put(LowcoderConfig.Database.PASSWORD, password);
  }
}
