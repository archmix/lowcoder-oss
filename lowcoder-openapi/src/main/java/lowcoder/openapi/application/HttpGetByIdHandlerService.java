package lowcoder.openapi.application;

import com.google.common.net.MediaType;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.HttpHandlerServiceSpecification;
import lowcoder.metadata.interfaces.Table;
import lowcoder.openapi.infra.HttpEndpointURIBuilder;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.interfaces.SearchOptions;

@Slf4j
@HttpHandlerServiceSpecification
public class HttpGetByIdHandlerService implements HttpHandlerService {

  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpGetByIdHandler instance = new HttpGetByIdHandler(pool, table);

    if (table.getPrimaryKeys().isEmpty()) {
      return;
    }

    String uri = HttpEndpointURIBuilder.create().from(table).build();
    log.info("Registering GET handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.GET, uri)
      .produces(MediaType.JSON_UTF_8.withoutParameters().toString())
      .handler(instance);
  }

  static class HttpGetByIdHandler extends AbstractHttpHandler {
    HttpGetByIdHandler(ConnectionPool pool, Table table) {
    super(pool, table);
  }

    public void handle(RoutingContext context, String requestId) {
      SearchOptions options = SearchOptions.create(table);
      options.from(context);

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
