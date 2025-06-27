package openAPIRouter;

import io.vertx.core.Vertx;
import io.vertx.junit5.VertxTestContext;
import lowcoder.testsuite.infra.FileAssertion;
import lowcoder.testsuite.infra.TableCleaner;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.hamcrest.Matchers.*;

class PersonAPITest extends PostgreBasedTest {
  @BeforeEach
  void beforeEach(Vertx vertx, VertxTestContext testContext) {
    TableCleaner.of().clean(vertx, "persons");
    testContext.completeNow();
  }

  @Test
  void givenAPersonWhenPostThenGetLocation(VertxTestContext testContext) {
    var person_json = FileAssertion.file("/persons/name_last.json").getContent();

    var location = toEndpointURI("persons");
    doPostAssertionAndGetLocation(location, person_json);

    person_json = FileAssertion.file("/persons/last_name.json").getContent();
    doPostAssertionAndGetLocation(location, person_json);

    testContext.completeNow();
  }

  @Test
  void givenAnExsitingPersonWhenPutThenGetNoContent(VertxTestContext testContext) {
    var person_json = FileAssertion.file("/persons/name_last.json").getContent();
    var location = doPostAssertionAndGetLocation(toEndpointURI("persons"), person_json);

    person_json = FileAssertion.file("/persons/last_name.json").getContent();
    doPutAssertion(location, person_json);

    testContext.completeNow();
  }

  @Test
  void givenAnExsitingPersonWhenPatchThenGetUpdatedPerson(VertxTestContext testContext) {
    var person_json = FileAssertion.file("/persons/name_last.json").getContent();
    var location = doPostAssertionAndGetLocation(toEndpointURI("persons"), person_json);

    person_json = FileAssertion.file("/persons/patch.json").getContent();
    doPatchAssertion(location, person_json)
      .body("last_name", equalTo("Smith Jr."))
      .body("name", equalTo("John"));

    testContext.completeNow();
  }

  @Test
  void givenAnExistingPersonWhenGetByIdThenGetPerson(VertxTestContext testContext) {
    var person_json = FileAssertion.file("/persons/name_last.json").getContent();
    var location = doPostAssertionAndGetLocation(toEndpointURI("persons"), person_json);

    doGetAssertion(location)
      .body("name", equalTo("John"))
      .body("last_name", equalTo("Smith"));

    testContext.completeNow();
  }

  @Test
  void givenAnExistingPersonWhenGetOnlyNameFieldThenGetPersonWithNameOnly(VertxTestContext testContext) {
    var person_json = FileAssertion.file("/persons/name_last.json").getContent();
    var location = toEndpointURI("persons");
    doPostAssertionAndGetLocation(location, person_json);

    doGetAssertion(location + "?fields=name")
      .body("data.collect { it.keySet().size() }", everyItem(equalTo(1)))
      .body("data.collect { it.keySet().iterator().next() }", everyItem(equalTo("name")));

    testContext.completeNow();
  }

  @Test
  void givenExistingPersonsWhenGetWithNameOrderingThenReturns(VertxTestContext testContext) {
    var location = toEndpointURI("persons");

    var han_solo = FileAssertion.file("/persons/han_solo.json").getContent();
    var mr_anderson = FileAssertion.file("/persons/mr_anderson.json").getContent();
    var luke_skywalker = FileAssertion.file("/persons/luke_skywalker.json").getContent();

    doPostAssertionAndGetLocation(location, han_solo);
    doPostAssertionAndGetLocation(location, mr_anderson);
    doPostAssertionAndGetLocation(location, luke_skywalker);

    doGetAssertion(location + "?sort=name")
      .body("total", equalTo(3))
      .body("data[0].name", equalTo("Han"))
      .body("data[1].name", equalTo("Luke"))
      .body("data[2].name", equalTo("Mr."));

    doGetAssertion(location + "?sort=-name")
      .body("total", equalTo(3))
      .body("data[2].name", equalTo("Han"))
      .body("data[1].name", equalTo("Luke"))
      .body("data[0].name", equalTo("Mr."));

    testContext.completeNow();
  }

  @Test
  void givenExistingPersonsWhenGetWithFilteringThenReturns(VertxTestContext testContext) {
    var location = toEndpointURI("persons");

    var john_smith = FileAssertion.file("/persons/john_smith.json").getContent();
    var john_wick = FileAssertion.file("/persons/john_wick.json").getContent();
    var luke_skywalker = FileAssertion.file("/persons/luke_skywalker.json").getContent();
    var han_solo = FileAssertion.file("/persons/han_solo.json").getContent();

    doPostAssertionAndGetLocation(location, john_smith);
    doPostAssertionAndGetLocation(location, john_wick);
    doPostAssertionAndGetLocation(location, luke_skywalker);
    doPostAssertionAndGetLocation(location, han_solo);

    doGetAssertion(location + "?sort=last_name&name[like]=%John%")
      .body("total", equalTo(2))
      .body("data[0].last_name", equalTo("Smith"))
      .body("data[1].last_name", equalTo("Wick"));

    testContext.completeNow();
  }
}