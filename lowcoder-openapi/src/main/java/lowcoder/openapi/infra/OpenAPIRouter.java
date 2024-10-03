package lowcoder.openapi.infra;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.ConnectionPool;

@RouterServiceSpecification
@Slf4j
public class OpenAPIRouter implements RouterService {

  @Override
  public void accept(Vertx vertx, Router router, ConnectionPool pool, Table table) {
    HttpPostHandler.register(router, pool, table);
    HttpPutHandler.register(router, pool, table);
    HttpPatchHandler.register(router, pool, table);
    HttpGetByIdHandler.register(router, pool, table);
    HttpGetByIdHandler.register(router, pool, table);
  }
}
