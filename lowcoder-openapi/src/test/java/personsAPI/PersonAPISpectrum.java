package personsAPI;

import lowcoder.testsuite.application.DomainBands;
import lowcoder.openapi.infra.OpenApiSpectrum;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static lowcoder.testsuite.infra.FileAssertion.*;
import static org.hamcrest.Matchers.*;

@SpectrumSpecification
public class PersonAPISpectrum extends OpenApiSpectrum {
  @Override
  public Band band() {
    return DomainBands.PERSONS;
  }

  @Override
  protected String[] resources() {
    return new String[]{PERSONS_RESOURCE_NAME};
  }

  @Ray
  void givenANewPersonWhenPostThenGetLocation() {
    var person_json = file("/persons/name_last.json").getContent();

    doPostAssertionAndGetLocation(PERSONS_RESOURCE, person_json);

    person_json = file("/persons/last_name.json").getContent();
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, person_json);
  }

  @Ray
  void givenAnExistingPersonWhenPutThenGetNoContent() {
    var person_json = file("/persons/name_last.json").getContent();
    var location = doPostAssertionAndGetLocation(PERSONS_RESOURCE, person_json);

    person_json = file("/persons/last_name.json").getContent();
    doPutAssertion(location, person_json);
  }

  @Ray
  void givenAnExistingPersonWhenPatchThenGetUpdatedPerson() {
    var person_json = file("/persons/name_last.json").getContent();
    var location = doPostAssertionAndGetLocation(PERSONS_RESOURCE, person_json);

    person_json = file("/persons/patch.json").getContent();
    doPatchAssertion(location, person_json)
      .body("last_name", equalTo("Smith Jr."))
      .body("name", equalTo("John"));
  }

  @Ray
  void givenAnExistingPersonWhenGetByIdThenGetPerson() {
    var person_json = file("/persons/name_last.json").getContent();
    var location = doPostAssertionAndGetLocation(PERSONS_RESOURCE, person_json);

    doGetAssertion(location)
      .body("name", equalTo("John"))
      .body("last_name", equalTo("Smith"));
  }

  @Ray
  void givenAnExistingPersonWhenGetOnlyNameFieldThenGetPersonWithNameOnly() {
    var person_json = file("/persons/name_last.json").getContent();
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, person_json);

    doGetAssertion(PERSONS_RESOURCE,
      request ->{
        request.param("fields", "name");
      })
      .body("data.collect { it.keySet().size() }", everyItem(equalTo(1)))
      .body("data.collect { it.keySet().iterator().next() }", everyItem(equalTo("name")));
  }

  @Ray
  void givenExistingPersonsWhenGetWithNameOrderingThenReturns() {
    var han_solo = file("/persons/han_solo.json").getContent();
    var mr_anderson = file("/persons/mr_anderson.json").getContent();
    var luke_skywalker = file("/persons/luke_skywalker.json").getContent();

    doPostAssertionAndGetLocation(PERSONS_RESOURCE, han_solo);
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, mr_anderson);
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, luke_skywalker);

    doGetAssertion(PERSONS_RESOURCE,
      request -> {
        request.param("sort", "name");
      })
      .body("total", equalTo(3))
      .body("data[0].name", equalTo("Han"))
      .body("data[1].name", equalTo("Luke"))
      .body("data[2].name", equalTo("Mr."));

    doGetAssertion(PERSONS_RESOURCE,
      request ->{
        request.param("sort", "-name");
      })
      .body("total", equalTo(3))
      .body("data[2].name", equalTo("Han"))
      .body("data[1].name", equalTo("Luke"))
      .body("data[0].name", equalTo("Mr."));
  }

  @Ray
  void givenExistingPersonsWhenGetWithFilteringThenReturns() {
    var john_smith = file("/persons/john_smith.json").getContent();
    var john_wick = file("/persons/john_wick.json").getContent();
    var luke_skywalker = file("/persons/luke_skywalker.json").getContent();
    var han_solo = file("/persons/han_solo.json").getContent();

    doPostAssertionAndGetLocation(PERSONS_RESOURCE, john_smith);
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, john_wick);
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, luke_skywalker);
    doPostAssertionAndGetLocation(PERSONS_RESOURCE, han_solo);

    doGetAssertion(PERSONS_RESOURCE,
      request ->{
        request.param("sort", "last_name");
        request.param("name[like]", "%John%");
      })
      .body("total", equalTo(2))
      .body("data[0].last_name", equalTo("Smith"))
      .body("data[1].last_name", equalTo("Wick"));
  }
}