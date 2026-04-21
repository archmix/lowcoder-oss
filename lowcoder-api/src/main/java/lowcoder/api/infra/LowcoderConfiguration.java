package lowcoder.api.infra;

import io.vertx.core.json.JsonObject;
import legolas.config.api.interfaces.Configuration;
import legolas.config.api.interfaces.Entry;
import legolas.net.core.interfaces.LocalPortBinding;
import legolas.net.core.interfaces.Port;
import legolas.net.core.interfaces.SocketType;

import static lowcoder.api.infra.LowcoderConfiguration.ConnectionPoolEntries.*;
import static lowcoder.api.infra.LowcoderConfiguration.DatabaseEntries.*;
import static lowcoder.api.infra.LowcoderConfiguration.HttpEntries.*;

public class LowcoderConfiguration {
  private final Configuration config = Configuration.create();

  private LowcoderConfiguration() {
    this.config.set(PORT, HttpEntries.getPortAvailable());
    this.config.set(MAX_SIZE, ConnectionPoolEntries.DEFAULT_CP_MAX_SIZE);
  }

  public static LowcoderConfiguration create() {
    return new LowcoderConfiguration();
  }

  public void set(Entry entry, Object value) {
    this.config.set(entry, value);
  }

  public Number getHttpPort() {
    return this.config.getNumber(PORT).orElse(DEFAULT_HTTP_PORT);
  }

  public Number getConnectionPoolMaxSize() {
    return this.config.getNumber(MAX_SIZE).orElse(DEFAULT_CP_MAX_SIZE);
  }

  public String getUrl() {
    return this.config.getString(DatabaseEntries.URL).orElse(null);
  }

  public String getDriver() {
    return this.config.getString(DRIVER).orElse(null);
  }

  public String getUser() {
    return this.config.getString(USER).orElse(null);
  }

  public String getPassword() {
    return this.config.getString(PASSWORD).orElse(null);
  }

  public String getSchema() {
    return this.config.getString(SCHEMA).orElse(null);
  }

  public void validate(){
    this.config.getString(DatabaseEntries.URL).orElseThrow(() -> new IllegalStateException("Missing database url"));
    this.config.getString(DatabaseEntries.DRIVER).orElseThrow(() -> new IllegalStateException("Missing database driver"));
    this.config.getString(DatabaseEntries.USER).orElseThrow(() -> new IllegalStateException("Missing database user"));
    this.config.getString(DatabaseEntries.PASSWORD).orElseThrow(() -> new IllegalStateException("Missing database password"));
  }

  public JsonObject toJson() {
    this.validate();
    var json = new JsonObject();
    this.config.toMap().forEach(json::put);
    return json;
  }

  public enum HttpEntries implements Entry {
    PORT;

    public static final int DEFAULT_HTTP_PORT = 8080;

    public static int getPortAvailable() {
      return LocalPortBinding.create(SocketType.TCP).nextPortAvailable(Port.create(DEFAULT_HTTP_PORT)).value();
    }

    public Integer get(JsonObject config) {
      return config.getNumber(this.value()).intValue();
    }

    @Override
    public String value() {
      return "HTTP_" + this.name();
    }

    @Override
    public String toString() {
      return this.value();
    }
  }

  public enum ConnectionPoolEntries implements Entry {
    MAX_SIZE;

    public static final int DEFAULT_CP_MAX_SIZE = 10;

    public Integer get(JsonObject config) {
      return config.getNumber(this.value()).intValue();
    }

    @Override
    public String value() {
      return "CP_" + this.name();
    }

    @Override
    public String toString() {
      return this.value();
    }
  }

  public enum DatabaseEntries implements Entry {
    URL,
    DRIVER,
    USER,
    PASSWORD,
    SCHEMA;

    public String get(JsonObject config) {
      return config.getString(this.value());
    }

    @Override
    public String value() {
      return "DB_" + this.name();
    }

    @Override
    public String toString() {
      return this.value();
    }
  }
}
