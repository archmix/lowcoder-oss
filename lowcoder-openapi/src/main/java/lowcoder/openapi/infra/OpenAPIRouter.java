package lowcoder.openapi.infra;

import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.RouterTableService;
import lowcoder.core.interfaces.RouterTableServiceSpecification;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.infra.ConnectionPool;
import morphos.api.interfaces.Table;

import java.util.ServiceLoader;

@RouterTableServiceSpecification
@Slf4j
public class OpenAPIRouter implements RouterTableService {

  @Override
  public void accept(Vertx vertx, Router router, ConnectionPool pool, Table table, FuturePromise<Void> promise) {
    ServiceLoader.load(HttpHandlerService.class).forEach(service -> {
      service.accept(router, pool, table);
    });
    promise.complete();
  }
}
