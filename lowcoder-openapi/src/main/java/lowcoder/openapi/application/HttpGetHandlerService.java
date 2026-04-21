package lowcoder.openapi.application;

import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.HttpHandlerServiceSpecification;
import lowcoder.api.infra.HttpEndpointURIBuilder;
import lowcoder.openapi.infra.MimeType;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.interfaces.SearchOptions;
import morphos.api.interfaces.Table;

@Slf4j
@HttpHandlerServiceSpecification
public class HttpGetHandlerService implements HttpHandlerService {

  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpGetHandler instance = new HttpGetHandler(pool, table);

    String uri = HttpEndpointURIBuilder.create().path(table.getName()).build();
    log.info("Registering GET handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.GET, uri)
      .produces(MimeType.JSON)
      .handler(instance);
  }

  static class HttpGetHandler extends AbstractHttpHandler {
    HttpGetHandler(ConnectionPool pool, Table table) {
      super(pool, table);
    }

    public void handle(RoutingContext context, String requestId) {
      log.info("GET request for table {}", table.getName());

      SearchOptions options = this.getSearchOptions(context);
      pool.selectCommand().findPaged(options, select ->{
        context.response()
          .setStatusCode(200)
          .putHeader("Content-Type", "application/json")
          .end(select.encode());
      }, errorHandler(context));
    }
  }
}
