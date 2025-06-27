package lowcoder.openapi.infra;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;
import lowcoder.metadata.interfaces.Table;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.infra.ConnectionPool;

import java.util.ServiceLoader;

@RouterServiceSpecification
@Slf4j
public class OpenAPIRouter implements RouterService {

  @Override
  public void accept(Vertx vertx, Router router, ConnectionPool pool, Table table, FuturePromise<Void> promise) {
    ServiceLoader.load(HttpHandlerService.class).forEach(service -> {
      service.accept(router, pool, table);
    });
    promise.complete();
  }
}
