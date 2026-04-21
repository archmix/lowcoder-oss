package lowcoder.api.application;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxExtension;
import legolas.provided.infra.LegolasExtension;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.LowcoderConfiguration;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith({VertxExtension.class, LegolasExtension.class})
@Slf4j
public class LowcoderTest {
  protected static LowcoderConfiguration config = LowcoderConfiguration.create();

  protected static void startLowcoder(Vertx vertx) {
    startLowcoder(vertx, handler -> {});
  }

  protected static void startLowcoder(Vertx vertx, Handler<Void> onComplete) {
    Integer httpPort = LowcoderConfiguration.HttpEntries.getPortAvailable();
    config.set(LowcoderConfiguration.HttpEntries.PORT, httpPort);

    LowcoderApplication.Deployer.create(config).deploy(vertx, onComplete, onFail ->{
      log.error("Failed to start lowcoder", onFail);
    });
  }

  protected static void setDatabaseConfig(String schema, String url, String driver, String username, String password) {
    config.set(LowcoderConfiguration.DatabaseEntries.SCHEMA, schema);
    config.set(LowcoderConfiguration.DatabaseEntries.URL, url);
    config.set(LowcoderConfiguration.DatabaseEntries.DRIVER, driver);
    config.set(LowcoderConfiguration.DatabaseEntries.USER, username);
    config.set(LowcoderConfiguration.DatabaseEntries.PASSWORD, password);
  }
}
