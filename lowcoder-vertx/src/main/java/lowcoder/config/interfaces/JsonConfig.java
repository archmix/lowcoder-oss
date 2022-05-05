package lowcoder.config.interfaces;

import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

import java.util.Optional;

@RequiredArgsConstructor
class JsonConfig implements Config {
    private final JsonObject values;

    @Override
    public JsonObject toJsonObject() {
        return this.values;
    }

    @Override
    public <T> T get(Entry<T> entry) {
        return (T) this.values.getValue(entry.value());
    }

    @Override
    public Object getObject(Entry<Object> entry) {
        return Optional.ofNullable(this.values.getValue(entry.value())).orElse(entry.defaultValue());
    }

    @Override
    public Boolean getBoolean(Entry<Boolean> entry) {
        return Optional.ofNullable(this.<Boolean>get(entry)).orElse(entry.defaultValue());
    }

    @Override
    public Number getNumber(Entry<Number> entry) {
        final Object value = this.values.getValue(entry.value());

        if(value == null) {
            return entry.defaultValue();
        }

        if (value instanceof String) {
            return Integer.valueOf((String) value);
        }

        return (Number) value;
    }

    @Override
    public String getString(Entry<String> entry) {
        final Object value = this.values.getValue(entry.value());

        if(value == null) {
            return entry.defaultValue();
        }

        if (value instanceof Number) {
            return String.valueOf(((Number) value).intValue());
        }

        return value.toString();
    }
}
