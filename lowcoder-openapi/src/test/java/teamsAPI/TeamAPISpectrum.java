package teamsAPI;

import io.vertx.core.json.JsonObject;
import lowcoder.testsuite.application.DomainBands;
import lowcoder.openapi.infra.OpenApiSpectrum;
import spectra.interfaces.Band;
import spectra.interfaces.Ray;
import spectra.interfaces.SpectrumSpecification;

import static lowcoder.testsuite.infra.FileAssertion.*;

@SpectrumSpecification
public class TeamAPISpectrum extends OpenApiSpectrum {
  @Override
  public Band band() {
    return DomainBands.TEAMS;
  }

  @Override
  protected String[] resources() {
    return new String[]{PERSONS_RESOURCE_NAME, COMPANIES_RESOURCE_NAME, TEAMS_RESOURCE_NAME};
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
}
