package lowcoder.core.interfaces;

import io.vertx.core.Vertx;

public interface DeployableService {
    void accept(Vertx vertx);
}
