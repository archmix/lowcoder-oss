package lowcoder.openapi.application;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.HttpHandlerServiceSpecification;
import lowcoder.openapi.infra.MimeType;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.interfaces.SearchOptions;
import morphos.api.interfaces.Table;

@Slf4j
@HttpHandlerServiceSpecification
public class HttpGetByIdHandlerService implements HttpHandlerService {

  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpGetByIdHandler instance = new HttpGetByIdHandler(pool, table);

    table.hasPrimaryKeys(pks -> {
      String uri = uri(table);
      log.info("Registering GET handler for table {} at {}", table.getName(), uri);

      router.route(HttpMethod.GET, uri)
        .produces(MimeType.JSON)
        .handler(instance);
    });
  }

  static class HttpGetByIdHandler extends AbstractHttpHandler {
    HttpGetByIdHandler(ConnectionPool pool, Table table) {
    super(pool, table);
  }

    public void handle(RoutingContext context, String requestId) {
      var options = this.getSearchOptions(context);

      log.info("GET request for table {}", table.getName());

      pool.selectCommand().findOne(options, select -> {
        context.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end(select.encodePrettily());
      }, errorHandler(context));
    }
  }
}
