package lowcoder.api.application;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import lowcoder.api.interfaces.ContainerService;

public abstract class AbstractContainerService implements ContainerService {
  protected Vertx vertx;

  @Override
  public final void init(Vertx vertx, Context context) {
    this.vertx = vertx;
    this.init();
  }

  protected void init() {}

  protected JsonObject  config(){
    return vertx.getOrCreateContext().config();
  }
}
