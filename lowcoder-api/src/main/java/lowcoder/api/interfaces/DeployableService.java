package lowcoder.api.interfaces;

import io.vertx.core.Vertx;

public interface DeployableService {
    void accept(Vertx vertx);
}
