package openAPIRouter;

import io.vertx.core.DeploymentOptions;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.junit5.VertxTestContext;
import lowcoder.core.application.LowcoderContainer;
import lowcoder.testsuite.infra.FileAssertion;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class PersonAPITest extends OpenAPIRouterTest {
  @BeforeAll
  static void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    setPostgre();
    vertx.deployVerticle(new LowcoderContainer(), new DeploymentOptions().setConfig(config), testContext.succeeding(id -> testContext.completeNow()));
  }

  @Test
  void givenAPersonWhenPostThenGetLocation(Vertx vertx, VertxTestContext testContext) {
    String person_json = FileAssertion.file("/persons/name_last.json").getContent();

    String location = toEndpointURI("persons");
    doPostAssertionAndGetLocation(location, person_json);

    person_json = FileAssertion.file("/persons/last_name.json").getContent();
    doPostAssertionAndGetLocation(location, person_json);

    testContext.completeNow();
  }

  @Test
  void givenAnExsitingPersonWhenPutThenGetNoContent(Vertx vertx, VertxTestContext testContext) {
    String person_json = FileAssertion.file("/persons/name_last.json").getContent();
    String location = doPostAssertionAndGetLocation(toEndpointURI("persons"), person_json);

    person_json = FileAssertion.file("/persons/last_name.json").getContent();
    doPutAssertion(location, person_json);

    testContext.completeNow();
  }

  @Test
  void givenAnExsitingPersonWhenPatchThenGetUpdatedPerson(Vertx vertx, VertxTestContext testContext) {
    String person_json = FileAssertion.file("/persons/name_last.json").getContent();
    String location = doPostAssertionAndGetLocation(toEndpointURI("persons"), person_json);

    person_json = FileAssertion.file("/persons/patch.json").getContent();
    JsonObject updatedPerson = doPatchAssertion(location, person_json);
    assertEquals("Smith Jr.", updatedPerson.getString("last_name"));
    assertEquals("John", updatedPerson.getString("name"));

    testContext.completeNow();
  }

  @Test
  void givenAnExistingPersonWhenGetByIdThenGetPerson(Vertx vertx, VertxTestContext testContext) {
    String person_json = FileAssertion.file("/persons/name_last.json").getContent();
    String location = doPostAssertionAndGetLocation(toEndpointURI("persons"), person_json);

    JsonObject person = doGetAssertion(location);
    assertEquals("John", person.getString("name"));
    assertEquals("Smith", person.getString("last_name"));

    testContext.completeNow();
  }
}
