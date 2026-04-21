package lowcoder.openapi.application;

import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.HttpHandlerService;
import lowcoder.core.interfaces.HttpHandlerServiceSpecification;
import lowcoder.openapi.infra.MimeType;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.infra.SelectSqlHandler;
import lowcoder.sql.infra.UpdateSqlHandler;
import lowcoder.sql.interfaces.SearchOptions;
import morphos.api.interfaces.Table;

@Slf4j
@HttpHandlerServiceSpecification
public class HttpPatchHandlerService implements HttpHandlerService {

  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpPatchHandler instance = new HttpPatchHandler(pool, table);

    table.hasPrimaryKeys(pks ->{
      var uri = uri(table);
      log.info("Registering PATCH handler for table {} at {}", table.getName(), uri);

      router.route(HttpMethod.PATCH, uri)
        .consumes(MimeType.JSON)
        .produces(MimeType.JSON)
        .handler(instance);
    });
  }

  static class HttpPatchHandler extends AbstractHttpHandler {
    HttpPatchHandler(ConnectionPool pool, Table table) {
      super(pool, table);
    }

    public void handle(RoutingContext context, String requestId) {
      context.request().bodyHandler(buffer -> {
        JsonObject json = buffer.toJsonObject();
        LoadIdFromContext.create().load(context, table, json);

        SearchOptions options = SearchOptions.create(SelectSqlHandler.of(table));

        log.info("PATCH request for table {}", table.getName());

        var handler = UpdateSqlHandler.of(table);
        pool.updateCommand(handler).execute(json, ok -> {
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
