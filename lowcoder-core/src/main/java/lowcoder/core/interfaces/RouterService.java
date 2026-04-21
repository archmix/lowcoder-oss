package lowcoder.core.interfaces;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.infra.ConnectionPool;
import morphos.api.interfaces.Table;

import java.util.Collection;

public interface RouterService {
    void accept(Vertx vertx, Router router, ConnectionPool pool, Collection<Table> tables, FuturePromise<Void> promise);
}
