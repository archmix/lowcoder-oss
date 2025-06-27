package lowcoder.openapi.application;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lowcoder.metadata.interfaces.Table;

public class LoadIdFromContext {
  private static LoadIdFromContext instance = new LoadIdFromContext();

  public static LoadIdFromContext of() {
    return instance;
  }

  public void load(RoutingContext context, Table table, JsonObject  json) {
    table.getPrimaryKeys().forEach(pk -> {
      String name = pk.getColumn().getName();
      String value = context.pathParam(name);
      json.put(name, value);
    });
  }
}
