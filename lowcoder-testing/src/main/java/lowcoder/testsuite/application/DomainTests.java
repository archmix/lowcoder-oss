package lowcoder.testsuite.application;

import io.vertx.core.Vertx;
import legolas.runtime.core.interfaces.RunningEnvironment;
import lowcoder.api.application.LowcoderTest;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import spectra.interfaces.Spectra;

import java.util.Collection;

public class DomainTests extends LowcoderTest {

  @TestFactory
  Collection<DynamicTest> testPersons(RunningEnvironment environment, Vertx vertx) {
    return Spectra.of().context(context -> {
      context.put("config", config);
      context.put("vertx", vertx);
    }).reveal(DomainBands.PERSONS);
  }

  @TestFactory
  Collection<DynamicTest> testTeams(RunningEnvironment environment, Vertx vertx) {
    return Spectra.of().context(context -> {
      context.put("config", config);
      context.put("vertx", vertx);
    }).reveal(DomainBands.TEAMS);
  }
}
