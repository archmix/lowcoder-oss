package lowcoder.core.interfaces;

import io.vertx.ext.web.Router;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.ConnectionPool;

public interface HttpHandlerService {
  void accept(Router router, ConnectionPool pool, Table table);
}
