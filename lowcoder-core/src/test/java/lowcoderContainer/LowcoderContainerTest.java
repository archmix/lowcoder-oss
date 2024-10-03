package lowcoderContainer;

import static io.restassured.RestAssured.*;
import static org.hamcrest.Matchers.*;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lowcoder.api.infra.ConfigEntries;
import lowcoder.core.application.LowcoderContainer;
import lowcoder.testsuite.infra.LowcoderTest;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class LowcoderContainerTest extends LowcoderTest {

  @BeforeEach
  void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    vertx.deployVerticle(new LowcoderContainer(), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void verticleDeployed(Vertx vertx, VertxTestContext testContext) throws Throwable {
    Number httpPort = ConfigEntries.HTTP.getPort(vertx.getOrCreateContext().config());
    Assertions.assertNotNull(httpPort);
    Assertions.assertTrue(httpPort.intValue() > 0);
    testContext.completeNow();
  }

  @Test
  void httpIsReady(Vertx vertx, VertxTestContext testContext) throws Throwable {
    given().port(ConfigEntries.HTTP.getPort(vertx.getOrCreateContext().config()))
      .when().get("/welcome")
      .then().statusCode(200)
      .body(is("Hello from Lowcoder OSS"));
  }
}
