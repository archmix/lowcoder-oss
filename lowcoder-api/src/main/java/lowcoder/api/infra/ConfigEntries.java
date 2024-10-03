package lowcoder.api.infra;

import io.vertx.core.json.JsonObject;
import legolas.net.core.interfaces.LocalPortBinding;
import legolas.net.core.interfaces.Port;
import legolas.net.core.interfaces.SocketType;

import java.util.Optional;

public class ConfigEntries {

  public static class HTTP {
    public static final int DEFAULT_HTTP_PORT = 8080;
    public static final String PORT = "HTTP_PORT";

    public static int getPort(JsonObject config) {
      Integer port = config.getInteger(PORT);
      return Optional.ofNullable(port).orElse(DEFAULT_HTTP_PORT);
    }

    public static int getPortAvailable() {
      return LocalPortBinding.create(SocketType.TCP).nextPortAvailable(Port.create(DEFAULT_HTTP_PORT)).value();
    }
  }

  public static class ConnectionPool {
    private static final String MAX_SIZE = "CP_MAX_SIZE";

    public static int getMaxSize(JsonObject config) {
      return Optional.ofNullable(config.getInteger(MAX_SIZE)).orElse(10);
    }
  }

  public static class Database {
    public static final String URL = "DB_URL";
    public static final String DRIVER = "DB_DRIVER";
    public static final String USER = "DB_USER";
    public static final String PASSWORD = "DB_PASSWORD";

    public static String getUrl(JsonObject config) {
      return config.getString(URL);
    }

    public static String getDriver(JsonObject config) {
      return config.getString(DRIVER);
    }

    public static String getUser(JsonObject config) {
      return config.getString(USER);
    }

    public static String getPassword(JsonObject config) {
      return config.getString(PASSWORD);
    }
  }
}
