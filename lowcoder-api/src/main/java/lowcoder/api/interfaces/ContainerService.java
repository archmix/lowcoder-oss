package lowcoder.api.interfaces;

import io.vertx.core.Context;
import io.vertx.core.Vertx;
import lowcoder.promise.interfaces.FuturePromise;

public interface ContainerService {
    void init(Vertx vertx,Context context);

    void start(FuturePromise<Void> startPromise);

    void stop( FuturePromise<Void> stopPromise);
}
