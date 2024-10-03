package lowcoder.openapi.infra;

import com.google.common.net.MediaType;
import io.vertx.core.buffer.Buffer;
import io.vertx.core.http.HttpMethod;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.RoutingContext;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Table;
import lowcoder.openapi.interfaces.AbstractHttpHandler;
import lowcoder.sql.infra.ConnectionPool;

import java.util.UUID;

@Slf4j
public class HttpPostHandler extends AbstractHttpHandler {

  public HttpPostHandler(ConnectionPool pool, Table table) {
    super(pool, table);
  }

  public static void register(Router router, ConnectionPool pool, Table table) {
    HttpPostHandler instance = new HttpPostHandler(pool, table);
    String uri = HttpEndpointURIBuilder.create().path(table.getName()).build();

    log.info("Registering POST handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.POST, uri)
      .consumes(MediaType.JSON_UTF_8.withoutParameters().toString())
      .handler(instance);
  }

  public void handle(RoutingContext context) {
    context.request().bodyHandler(buffer -> {
      UUID requestId = UUID.randomUUID();

      log.info("POST request for table {}", table.getName());

      if(table.getPrimaryKeys().isEmpty()) {
        handlePost(context, buffer, requestId);
        return;
      }

      handleCreated(context, buffer, requestId);
    });
  }

  private void handlePost(RoutingContext context, Buffer buffer, UUID requestId) {
    pool.insertCommand(table).execute(buffer.toJsonObject(), ok ->{
      log.info("POST request for table {} executed", table.getName());
      context.response().setStatusCode(200).end();
    }, error ->{
      log.error("POST request for table {} failed", table.getName(), error);
      HttpErrorResponse.handleError(context.response(), requestId, error);
    });
  }

  private void handleCreated(RoutingContext context, Buffer buffer, UUID requestId) {
    pool.insertCommand(table).executeAndGetGeneratedKeys(buffer.toJsonObject(), ids ->{
      String[] values = ids.stream().map(id -> id.getValue().toString()).toArray(String[]::new);

      String locationURI = HttpEndpointURIBuilder.create().path(table.getName()).path(values).build();

      log.info("POST request for table {} executed and generated id {}", table.getName(), values);

      context.response()
        .setStatusCode(201)
        .putHeader("Location", locationURI)
        .end();
    }, error -> {
      log.error("POST request for table {} failed", table.getName(), error);
      HttpErrorResponse.handleError(context.response(), requestId, error);
    });
  }
}
