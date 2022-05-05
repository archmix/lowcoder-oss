package lowcoder.config.interfaces;

import io.vertx.core.json.JsonObject;

public interface Config {
    <T> T get(Entry<T> entry);

    Object getObject(Entry<Object> entry);

    Boolean getBoolean(final Entry<Boolean> entry);

    Number getNumber(final Entry<Number> entry);

    String getString(final Entry<String> entry);

    static interface Entry<T> {
        String value();

        T defaultValue();
    }

    JsonObject toJsonObject();
}
