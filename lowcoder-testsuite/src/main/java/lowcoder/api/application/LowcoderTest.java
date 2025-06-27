package lowcoder.api.application;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Handler;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxExtension;
import io.vertx.junit5.VertxTestContext;
import org.junit.jupiter.api.extension.ExtendWith;

@ExtendWith(VertxExtension.class)
public class LowcoderTest {
  protected static JsonObject config = new JsonObject();

  public static void startLowcoder(Vertx vertx, VertxTestContext testContext) {
    startLowcoder(vertx, testContext, handler -> {});
  }

  public static void startLowcoder(Vertx vertx, VertxTestContext testContext, Handler<Void> onComplete) {
    vertx.deployVerticle(new LowcoderContainer(), new DeploymentOptions().setConfig(config),
      testContext.succeeding(id -> {
        onComplete.handle(null);
        testContext.completeNow();
      }));
  }
}
