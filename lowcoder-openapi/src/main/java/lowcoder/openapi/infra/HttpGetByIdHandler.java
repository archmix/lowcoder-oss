package lowcoder.openapi.infra;

import com.google.common.net.MediaType;
import io.vertx.core.Handler;
import io.vertx.core.http.HttpMethod;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Table;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.interfaces.SearchOptions;

import java.util.UUID;

@Slf4j
public class HttpGetByIdHandler extends AbstractHttpHandler {
  HttpGetByIdHandler(ConnectionPool pool, Table table) {
    super(pool, table);
  }

  public static void register(Router router, ConnectionPool pool, Table table) {
    HttpGetByIdHandler instance = new HttpGetByIdHandler(pool, table);

    if(table.getPrimaryKeys().isEmpty()) {
      return;
    }

    String uri = HttpEndpointURIBuilder.create().from(table).build();
    log.info("Registering GET handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.GET, uri)
      .produces(MediaType.JSON_UTF_8.withoutParameters().toString())
      .handler(instance);
  }

  public void handle(RoutingContext context) {
    UUID requestId = UUID.randomUUID();

    SearchOptions options = SearchOptions.create();

    table.getPrimaryKeys().forEach(pk -> {
      String name = pk.getColumn().getName();
      String value = context.pathParam(name);
      options.getFilter().put(name, value);
    });

    log.info("GET request for table {}", table.getName());

    pool.selectByIdCommand(table).execute(options, select ->{
      context.response()
        .setStatusCode(200)
        .end(select.encodePrettily());
    }, errorHandler(context));
  }

  private Handler<Throwable> errorHandler(RoutingContext context) {
    return error -> {
      log.error("GET request for table {} failed", table.getName(), error);
      HttpErrorResponse.handleError(context.response(), UUID.randomUUID(), error);
    };
  }
}
