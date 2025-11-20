package lowcoder.openapi.application;

import io.vertx.core.Vertx;
import legolas.config.api.interfaces.Configuration;
import legolas.postgre.interfaces.PostgreSQLEntry;
import legolas.postgre.interfaces.PostgreSQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import lowcoder.testsuite.application.DomainTests;
import org.junit.jupiter.api.BeforeAll;

public class PostgreSQLTestSuite extends DomainTests {
  @BeforeAll
  public static void beforeAll(RunningEnvironment environment, Vertx vertx) {
    setPostgreConfig(environment);
    startLowcoder(vertx);
  }

  private static void setPostgreConfig(RunningEnvironment environment) {
    Configuration config = environment.get(PostgreSQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(PostgreSQLEntry.URL).get();
    String driver = config.getString(PostgreSQLEntry.DRIVER).get();
    String username = config.getString(PostgreSQLEntry.USERNAME).get();
    String password = config.getString(PostgreSQLEntry.PASSWORD).get();

    setDatabaseConfig("public",url, driver, username, password);
  }
}
