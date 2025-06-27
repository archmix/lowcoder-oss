package lowcoder.openapi.application;

import com.google.common.net.MediaType;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.HttpHandlerServiceSpecification;
import lowcoder.metadata.interfaces.Table;
import lowcoder.openapi.infra.HttpEndpointURIBuilder;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;

@Slf4j
@HttpHandlerServiceSpecification
public class HttpPutHandlerService implements HttpHandlerService {
  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpPutHandler instance = new HttpPutHandler(pool, table);

    if(table.getPrimaryKeys().isEmpty()) {
      return;
    }

    String uri = HttpEndpointURIBuilder.create().from(table).build();
    log.info("Registering PUT handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.PUT, uri)
      .consumes(MediaType.JSON_UTF_8.withoutParameters().toString())
      .handler(instance);
  }

  static class HttpPutHandler extends AbstractHttpHandler {
    HttpPutHandler(ConnectionPool pool, Table table) {
      super(pool, table);
    }

    public void handle(RoutingContext context, String requestId) {
      context.request().bodyHandler(buffer -> {
        JsonObject json = buffer.toJsonObject();
        LoadIdFromContext.of().load(context, table, json);

        log.info("PUT request for table {}", table.getName());

        pool.updateCommand(table).execute(json, ok -> {
          log.info("PUT request for table {} executed", table.getName());
          context.response()
            .setStatusCode(204)
            .end();
        }, errorHandler(context));
      });
    }
  }
}
