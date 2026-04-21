package lowcoder.graphql.application;

import graphql.GraphQL;
import io.vertx.core.Vertx;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.BodyHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandler;
import io.vertx.ext.web.handler.graphql.GraphQLHandlerOptions;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import lombok.extern.slf4j.Slf4j;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;
import lowcoder.graphql.infra.SchemaBuilder;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.infra.ConnectionPool;
import morphos.api.interfaces.Table;

import java.util.Collection;

@RouterServiceSpecification
@Slf4j
public class GraphQLRouterService implements RouterService {
  @Override
  public void accept(Vertx vertx, Router router, ConnectionPool pool, Collection<Table> tables, FuturePromise<Void> promise) {
    var graphiQLHandler = GraphiQLHandler.builder(vertx)
      .with(new GraphiQLHandlerOptions().setEnabled(true))
      .build();

    router.route("/graphiql*").subRouter(graphiQLHandler.router());
    router.route().handler(BodyHandler.create());

    var options = new GraphQLHandlerOptions().setRequestBatchingEnabled(true);

    var runtimeWiring = RuntimeWiringBuilder.of(tables, pool).build();
    var schema = SchemaBuilder.create(tables).build(runtimeWiring);

    var graphQL = GraphQL.newGraphQL(schema).build();
    var graphQLHandler = GraphQLHandler.create(graphQL, options);
    router.post("/graphql").handler(graphQLHandler);

    log.info("GraphQL router is ready");

    promise.complete();
  }
}