package openAPIRouter;

import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lowcoder.api.infra.ConfigEntries;
import lowcoder.openapi.infra.HttpEndpointURIBuilder;
import lowcoder.core.application.EnvironmentLowcoderTest;
import org.junit.jupiter.api.BeforeAll;

public abstract class PostgreBasedTest extends EnvironmentLowcoderTest {
  protected RequestSpecification given() {
    return RestAssured.given().port(ConfigEntries.HTTP.getPort(config));
  }

  protected String toEndpointURI(String resource) {
    return HttpEndpointURIBuilder.create().path(resource).build();
  }

  @BeforeAll
  static void deployVerticle(Vertx vertx, VertxTestContext testContext) {
    setPostgre();
    startLowcoder(vertx, testContext);
  }

  protected String doPostAssertionAndGetLocation(String location, String json) {
    return given().body(json).with().contentType("application/json")
      .when().post(location)
      .then().statusCode(201)
      .log().all()
      .header("Location", notNullValue())
      .extract().header("Location");
  }

  protected void doPutAssertion(String location, String payload) {
    given().body(payload).with().contentType("application/json")
      .when().put(location)
      .then().statusCode(204)
      .log().all();
  }

  protected ValidatableResponse doPatchAssertion(String location, String payload) {
    return given().body(payload).with().contentType("application/json")
      .accept("application/json")
      .when().patch(location)
      .then().statusCode(200)
      .log().all()
      .body(notNullValue());
  }

  protected ValidatableResponse doGetAssertion(String location) {
    return given()
      .accept("application/json")
      .when().get(location)
      .then().statusCode(200)
      .log().all()
      .body(notNullValue());
  }
}