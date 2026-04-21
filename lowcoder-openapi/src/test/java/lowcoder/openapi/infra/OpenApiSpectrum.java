package lowcoder.openapi.infra;

import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import io.restassured.specification.RequestSpecification;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lowcoder.testsuite.infra.TableCleaner;
import spectra.interfaces.Cooldown;
import spectra.interfaces.Spectrum;

import java.util.function.Consumer;

import static lowcoder.api.infra.LowcoderConfiguration.HttpEntries.*;
import static org.hamcrest.Matchers.*;

public abstract class OpenApiSpectrum extends Spectrum {
  protected static final String COMPANIES_RESOURCE_NAME = "companies";
  protected static final HttpUri.Resource COMPANIES_RESOURCE = HttpUri.Resource.of(COMPANIES_RESOURCE_NAME);

  protected static final String PERSONS_RESOURCE_NAME = "persons";
  protected static final HttpUri.Resource PERSONS_RESOURCE = HttpUri.Resource.of(PERSONS_RESOURCE_NAME);

  protected static final String TEAMS_RESOURCE_NAME = "teams";
  protected static final HttpUri.Resource TEAMS_RESOURCE = HttpUri.Resource.of(TEAMS_RESOURCE_NAME);

  @Cooldown
  void cooldown() {
    TableCleaner.create().clean(vertx(), resources());
  }

  protected abstract String[] resources();

  private RequestSpecification given() {
    return RestAssured.given().port(PORT.get(config())).log().all();
  }

  protected HttpUri.Location doPostAssertionAndGetLocation(HttpUri.Resource resource, String json) {
    var location = given().body(json).with().contentType("application/json")
      .when().post(resource.get())
      .then().log().all()
      .statusCode(201)
      .header("Location", notNullValue())
      .extract().header("Location");

    return HttpUri.Location.of(location);
  }

  protected void doPutAssertion(HttpUri.Location location, String payload) {
    given().body(payload).with().contentType("application/json")
      .when().put(location.get())
      .then().log().all()
      .statusCode(204);
  }

  protected ValidatableResponse doPatchAssertion(HttpUri.Location location, String payload) {
    return given().body(payload).with().contentType("application/json")
      .accept("application/json")
      .when().patch(location.get())
      .then().log().all()
      .statusCode(200)
      .body(notNullValue());
  }

  protected ValidatableResponse doGetAssertion(HttpUri uri) {
    return doGetAssertion(uri, request -> {});
  }

  protected ValidatableResponse doGetAssertion(HttpUri uri, Consumer<RequestSpecification> decorator) {
    var request = given().with().contentType("application/json");
    decorator.accept(request);

    return request.when().get(uri.get())
    .then().log().all()
    .statusCode(200)
    .body(notNullValue());
  }

  protected Vertx vertx() {
    return context("vertx", Vertx.class);
  }

  private JsonObject config() {
    return context("config", JsonObject.class);
  }
}
