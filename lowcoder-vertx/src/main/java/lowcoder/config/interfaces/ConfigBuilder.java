package lowcoder.config.interfaces;

import io.vertx.config.ConfigRetriever;
import io.vertx.config.ConfigRetrieverOptions;
import io.vertx.config.ConfigStoreOptions;
import io.vertx.core.Future;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lowcoder.common.interfaces.Values;
import lowcoder.promise.interfaces.PromiseHandler;

public class ConfigBuilder {
    public static final String CONFIG_DIRECTORY = "conf/";

    private final ConfigRetrieverOptions target;

    private ConfigBuilder() {
        this.target = new ConfigRetrieverOptions();
    }

    public static ConfigBuilder create() {
        return new ConfigBuilder();
    }

    public ConfigBuilder withFile() {
        return this.withFileStore(Values.append(CONFIG_DIRECTORY, "application.yml")).asYaml().and();
    }

    public ConfigBuilder withFile(String path) {
        return this.withFileStore(path).asYaml().and();
    }

    FileStoreBuilder withFileStore(String path) {
        return new FileStoreBuilder(path);
    }

    public ConfigBuilder withEnvironmentVariables() {
        ConfigStoreOptions store = new ConfigStoreOptions().setType("env");
        this.target.addStore(store);
        return this;
    }

    public ConfigBuilder withSystemVariables() {
        ConfigStoreOptions store = new ConfigStoreOptions().setType("sys");
        this.target.addStore(store);
        return this;
    }

    public void build(Vertx vertx, PromiseHandler<Config> configHandler) {
        ConfigRetriever.create(vertx, this.target).getConfig(result -> {
            if (result.succeeded()) {
                configHandler.handle(Future.succeededFuture(new JsonConfig(result.result())));
                return;
            }
            configHandler.handle(Future.failedFuture(result.cause()));
        });
    }

    public class FileStoreBuilder {
        private final String path;

        public FileStoreBuilder(String path) {
            this.path = path;
        }

        public FileStoreBuilder asProperties() {
            return this.add("properties");
        }

        public FileStoreBuilder asJson() {
            return this.add("json");
        }

        public FileStoreBuilder asYaml() {
            return this.add("yaml");
        }

        public FileStoreBuilder asHocon() {
            return this.add("hocon");
        }

        private FileStoreBuilder add(String format) {
            ConfigStoreOptions options = new ConfigStoreOptions().setType("file");
            options.setConfig(new JsonObject().put("path", path));
            options.setFormat(format);
            this.and().target.addStore(options);

            return this;
        }

        public ConfigBuilder and() {
            return ConfigBuilder.this;
        }
    }
}
