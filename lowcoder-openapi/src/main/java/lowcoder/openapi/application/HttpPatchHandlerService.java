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
import lowcoder.sql.interfaces.SearchOptions;

@Slf4j
@HttpHandlerServiceSpecification
public class HttpPatchHandlerService implements HttpHandlerService {

  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpPatchHandler instance = new HttpPatchHandler(pool, table);

    if (table.getPrimaryKeys().isEmpty()) {
      return;
    }

    String uri = HttpEndpointURIBuilder.create().from(table).build();
    log.info("Registering PATCH handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.PATCH, uri)
      .consumes(MediaType.JSON_UTF_8.withoutParameters().toString())
      .produces(MediaType.JSON_UTF_8.withoutParameters().toString())
      .handler(instance);
  }

  static class HttpPatchHandler extends AbstractHttpHandler {
    HttpPatchHandler(ConnectionPool pool, Table table) {
      super(pool, table);
    }

    public void handle(RoutingContext context, String requestId) {
      context.request().bodyHandler(buffer -> {
        JsonObject json = buffer.toJsonObject();
        LoadIdFromContext.of().load(context, table, json);

        SearchOptions options = SearchOptions.create(table);
        options.from(context);

        log.info("PATCH request for table {}", table.getName());

        pool.updateCommand(table).execute(json, ok -> {
          log.info("PATCH request for table {} executed", table.getName());

          pool.selectCommand().findOne(options, rows -> {
            context.response()
              .setStatusCode(200)
              .putHeader("Content-Type", "application/json")
              .end(rows.encode());
          }, errorHandler(context));
        }, errorHandler(context));
      });
    }
  }
}
