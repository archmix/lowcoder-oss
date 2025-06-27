package lowcoder.api.interfaces;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lowcoder.promise.interfaces.FuturePromise;

public interface StartupService {
  void accept(Vertx vertx, Router router, FuturePromise<Void> futurePromise);
}
