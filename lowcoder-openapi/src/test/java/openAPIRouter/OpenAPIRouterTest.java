package openAPIRouter;

import static org.hamcrest.Matchers.*;

import io.restassured.RestAssured;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.json.JsonObject;
import lowcoder.api.infra.ConfigEntries;
import lowcoder.openapi.infra.HttpEndpointURIBuilder;
import lowcoder.testsuite.infra.EnvironmentLowcoderTest;

public abstract class OpenAPIRouterTest extends EnvironmentLowcoderTest {

  protected RequestSpecification given() {
    return RestAssured.given().port(ConfigEntries.HTTP.getPort(config));
  }

  protected String toEndpointURI(String resource) {
    return HttpEndpointURIBuilder.create().path(resource).build();
  }

  protected String doPostAssertionAndGetLocation(String location, String json) {
    return given().body(json).with().contentType("application/json")
      .when().post(location)
      .then().statusCode(201)
      .header("Location", notNullValue())
      .extract().header("Location");
  }

  protected void doPutAssertion(String location, String payload) {
    given().body(payload).with().contentType("application/json")
      .when().put(location)
      .then().statusCode(204);
  }

  protected JsonObject doPatchAssertion(String location, String payload) {
    String responseBody = given().body(payload).with().contentType("application/json")
      .accept("application/json")
      .when().patch(location)
      .then().statusCode(200)
      .body(notNullValue()).extract().asString();

    return new JsonObject(responseBody);
  }

  protected JsonObject doGetAssertion(String location) {
    String responseBody = given()
      .accept("application/json")
      .when().get(location)
      .then().statusCode(200)
      .body(notNullValue()).extract().asString();

    return new JsonObject(responseBody);
  }

}
