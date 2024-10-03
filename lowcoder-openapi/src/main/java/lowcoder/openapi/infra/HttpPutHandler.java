package lowcoder.openapi.infra;

import com.google.common.net.MediaType;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Table;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;

import java.util.UUID;

@Slf4j
public class HttpPutHandler extends AbstractHttpHandler {
  HttpPutHandler(ConnectionPool pool, Table table) {
    super(pool, table);
  }

  public static void register(Router router, ConnectionPool pool, Table table) {
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

  public void handle(RoutingContext context) {
    context.request().bodyHandler(buffer -> {
      UUID requestId = UUID.randomUUID();

      JsonObject json = buffer.toJsonObject();
      table.getPrimaryKeys().forEach(pk -> {
        String name = pk.getColumn().getName();
        String value = context.pathParam(name);
        json.put(name, value);
      });

      log.info("PUT request for table {}", table.getName());

      pool.updateCommand(table).execute(json, ok ->{
        log.info("PUT request for table {} executed", table.getName());
        context.response()
          .setStatusCode(204)
          .end();
      }, error -> {
        log.error("PUT request for table {} failed", table.getName(), error);
        HttpErrorResponse.handleError(context.response(), requestId, error);
      });
    });
  }
}
