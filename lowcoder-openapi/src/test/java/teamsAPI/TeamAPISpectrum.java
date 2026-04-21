package teamsAPI;

import io.vertx.core.json.JsonObject;
import lowcoder.testsuite.application.DomainBands;
import lowcoder.openapi.infra.OpenApiSpectrum;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static lowcoder.testsuite.infra.FileAssertion.*;
import static org.hamcrest.Matchers.*;

@SpectrumSpecification
public class TeamAPISpectrum extends OpenApiSpectrum {
  @Override
  public Band band() {
    return DomainBands.TEAMS;
  }

  @Override
  protected String[] resources() {
    return new String[]{TEAMS_RESOURCE_NAME, PERSONS_RESOURCE_NAME, COMPANIES_RESOURCE_NAME};
  }

  @Ray
  void givenANewTeamWhenPostThenGetLocation() {
    var manager_json = file("/persons/morpheus.json").getContent();
    var manager_location = doPostAssertionAndGetLocation(PERSONS_RESOURCE, manager_json);

    var company_json = file("/companies/acme.json").getContent();
    var company_location = doPostAssertionAndGetLocation(COMPANIES_RESOURCE, company_json);

    JsonObject teamJson = new JsonObject();
    teamJson.put("name", "New Team");
    teamJson.put("manager_id", manager_location.id());
    teamJson.put("company_id", company_location.id());

    doPostAssertionAndGetLocation(TEAMS_RESOURCE, teamJson.encodePrettily());
  }

  @Ray
  void givenAnExistingTeamWhenGetThenReturnsExpandedEntities() {
    var manager_json = file("/persons/morpheus.json").getContent();
    var manager_location = doPostAssertionAndGetLocation(PERSONS_RESOURCE, manager_json);

    var company_json = file("/companies/acme.json").getContent();
    var company_location = doPostAssertionAndGetLocation(COMPANIES_RESOURCE, company_json);

    JsonObject teamJson = new JsonObject();
    teamJson.put("name", "Existing Team");
    teamJson.put("manager_id", manager_location.idAsNumber());
    teamJson.put("company_id", company_location.idAsNumber());

    var team_location = doPostAssertionAndGetLocation(TEAMS_RESOURCE, teamJson.encodePrettily());

    doGetAssertion(TEAMS_RESOURCE,
      request -> {
        request.param("expand", "manager_id");
      })
      .body("total", equalTo(1))
      .body("data[0].id", equalTo(team_location.idAsNumber()))
      .body("data[0].name", equalTo("Existing Team"))
      .body("data[0].manager_id.id", equalTo(manager_location.idAsNumber()))
      .body("data[0].manager_id.name", equalTo("Morpheus"))
      .body("data[0].manager_id.last_name", equalTo("Fishburne"))
      .body("data[0].company_id", equalTo(company_location.idAsNumber()));
  }

  @Ray
  void givenAnExistingTeamWhenGetOnlyNameFieldThenReturnsTeamNameAndManagerInfo() {
    var manager_json = file("/persons/morpheus.json").getContent();
    var manager_location = doPostAssertionAndGetLocation(PERSONS_RESOURCE, manager_json);

    var company_json = file("/companies/acme.json").getContent();
    var company_location = doPostAssertionAndGetLocation(COMPANIES_RESOURCE, company_json);

    JsonObject teamJson = new JsonObject();
    teamJson.put("name", "Existing Team");
    teamJson.put("manager_id", manager_location.idAsNumber());
    teamJson.put("company_id", company_location.idAsNumber());

    doPostAssertionAndGetLocation(TEAMS_RESOURCE, teamJson.encodePrettily());

    doGetAssertion(TEAMS_RESOURCE,
      request -> {
        request.param("expand", "manager_id");
        request.param("fields", "name");
      })
      .body("total", equalTo(1))
      .body("data[0]", not(hasKey("id")))
      .body("data[0].name", equalTo("Existing Team"))
      .body("data[0].manager_id.id", equalTo(manager_location.idAsNumber()))
      .body("data[0].manager_id.name", equalTo("Morpheus"))
      .body("data[0].manager_id.last_name", equalTo("Fishburne"))
      .body("data[0]", not(hasKey("company_id")));
  }
}
