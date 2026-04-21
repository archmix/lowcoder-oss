package lowcoder.openapi.interfaces;

import io.vertx.core.MultiMap;

import java.util.Collection;

public enum GetParameters {
    FIELDS("fields"),
    EXPAND("expand"),
    LIMIT("limit"),
    OFFSET("offset"),
    SORT("sort"),
    OR("_or");

    private final String value;

    GetParameters(String value){
      this.value = value;
    }

    public String value() {
      return this.value;
    }

    public String get(MultiMap params) {
      return params.get(this.value);
    }

    public Collection<String> getAll(MultiMap params) {
      return params.getAll(this.value);
    }
  }