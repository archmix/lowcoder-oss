package lowcoder.testsuite.infra;

import io.vertx.core.json.JsonObject;
import legolas.config.api.interfaces.Configuration;
import legolas.postgre.interfaces.PostgreSQLEntry;
import legolas.postgre.interfaces.PostgreSQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import legolas.runtime.core.interfaces.RuntimeEnvironment;
import lowcoder.api.infra.ConfigEntries;
import org.junit.jupiter.api.BeforeAll;

import java.util.concurrent.Executors;

public abstract class EnvironmentLowcoderTest extends LowcoderTest {
  protected static RunningEnvironment environment;

  protected static JsonObject config = new JsonObject();

  @BeforeAll
  public static void startEnvironment() {
    environment = RuntimeEnvironment.TEST.start(Executors.newSingleThreadExecutor()).get();
  }

  protected static void setPostgre() {
    Configuration config = environment.get(PostgreSQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(PostgreSQLEntry.URL).get();
    String driver = config.getString(PostgreSQLEntry.DRIVER).get();
    String username = config.getString(PostgreSQLEntry.USERNAME).get();
    String password = config.getString(PostgreSQLEntry.PASSWORD).get();

    setConfig(url, driver, username, password);
  }

  private static void setConfig(String url, String driver, String username, String password) {
    Integer httpPort = ConfigEntries.HTTP.getPortAvailable();
    config.put(ConfigEntries.HTTP.PORT, httpPort);

    config.put(ConfigEntries.Database.URL, url);
    config.put(ConfigEntries.Database.DRIVER, driver);
    config.put(ConfigEntries.Database.USER, username);
    config.put(ConfigEntries.Database.PASSWORD, password);
  }
}
