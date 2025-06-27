package lowcoder.graphql.infra;

import com.google.common.net.MediaType;
import io.vertx.core.Vertx;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;
import lowcoder.metadata.interfaces.Table;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.infra.ConnectionPool;

@RouterServiceSpecification
public class GraphQLRouter implements RouterService {
  private GraphiQLHandler handler;

  @Override
  public void accept(Vertx vertx, Router router, ConnectionPool pool, Table table, FuturePromise<Void> promise) {
    GraphiQLHandlerOptions options = new GraphiQLHandlerOptions().setEnabled(true);

    GraphiQLHandler handler = GraphiQLHandler.builder(vertx)
      .with(options)
      .build();

    router.route("/graphiql*").subRouter(handler.router());
    promise.complete();
  }
}
