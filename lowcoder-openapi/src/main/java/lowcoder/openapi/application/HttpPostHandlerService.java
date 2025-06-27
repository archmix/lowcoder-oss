package lowcoder.openapi.application;

import com.google.common.net.MediaType;
import io.vertx.core.buffer.Buffer;
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

@HttpHandlerServiceSpecification
@Slf4j
public class HttpPostHandlerService implements HttpHandlerService {

  public void accept(Router router, ConnectionPool pool, Table table) {
    HttpPostHandler instance = new HttpPostHandler(pool, table);
    String uri = HttpEndpointURIBuilder.create().path(table.getName()).build();

    log.info("Registering POST handler for table {} at {}", table.getName(), uri);

    router.route(HttpMethod.POST, uri)
      .consumes(MediaType.JSON_UTF_8.withoutParameters().toString())
      .handler(instance);
  }

  static class HttpPostHandler extends AbstractHttpHandler {

    HttpPostHandler(ConnectionPool pool, Table table) {
      super(pool, table);
    }

    public void handle(RoutingContext context, String requestId) {
      context.request().bodyHandler(buffer -> {
        log.info("POST request for table {}", table.getName());

        if (table.getPrimaryKeys().isEmpty()) {
          handlePost(context, buffer);
          return;
        }

        handleCreated(context, buffer);
      });
    }

    private void handlePost(RoutingContext context, Buffer buffer) {
      pool.insertCommand(table).execute(buffer.toJsonObject(), ok -> {
        log.info("POST request for table {} executed", table.getName());
        context.response().setStatusCode(200).end();
      }, errorHandler(context));
    }

    private void handleCreated(RoutingContext context, Buffer buffer) {
      pool.insertCommand(table).executeAndGetGeneratedKeys(buffer.toJsonObject(), ids -> {
        String[] values = ids.stream().map(id -> id.getValue().toString()).toArray(String[]::new);

        String locationURI = HttpEndpointURIBuilder.create().path(table.getName()).path(values).build();

        log.info("POST request for table {} executed and generated id {}", table.getName(), values);

        context.response()
          .setStatusCode(201)
          .putHeader("Location", locationURI)
          .end();
      }, errorHandler(context));
    }
  }
}
