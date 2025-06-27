package lowcoder.core.interfaces;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.metadata.interfaces.Table;

public interface RouterService {
    void accept(Vertx vertx, Router router, ConnectionPool pool, Table table, FuturePromise<Void> promise);
}
