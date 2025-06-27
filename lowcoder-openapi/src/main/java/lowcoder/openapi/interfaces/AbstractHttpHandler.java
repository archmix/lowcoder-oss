package lowcoder.openapi.interfaces;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.ConnectionPool;

import static lowcoder.core.application.LowcoderStarter.*;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractHttpHandler implements HttpHandler {
  protected final ConnectionPool pool;
  protected final Table table;

  protected Handler<Throwable> errorHandler(RoutingContext context) {
    return error -> {
      log.error("{} request for table {} failed", context.request().method(), table.getName(), error);
      HttpErrorResponse.handleError(context, error);
    };
  }

  @Override
  public final void handle(RoutingContext routingContext) {
    String requestId = routingContext.get(X_REQUEST_ID);
    this.handle(routingContext, requestId);
  }

  protected abstract void handle(RoutingContext context, String requestId);
}
