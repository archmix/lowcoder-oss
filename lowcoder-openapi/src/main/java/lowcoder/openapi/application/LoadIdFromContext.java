package lowcoder.openapi.application;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import morphos.api.interfaces.Table;

public class LoadIdFromContext {
  private static final LoadIdFromContext INSTANCE = new LoadIdFromContext();

  public static LoadIdFromContext create() {
    return INSTANCE;
  }

  public void load(RoutingContext context, Table table, JsonObject  json) {
    table.getPrimaryKeys().forEach(pk -> {
      var name = pk.getName();
      var value = context.pathParam(name);
      json.put(name, value);
    });
  }
}
