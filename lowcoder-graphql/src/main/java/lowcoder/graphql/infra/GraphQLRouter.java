package lowcoder.graphql.infra;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import io.vertx.ext.web.handler.graphql.GraphiQLHandler;
import io.vertx.ext.web.handler.graphql.GraphiQLHandlerOptions;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;

@RouterServiceSpecification
public class GraphQLRouter implements RouterService {

    @Override
    public void accept(Router router) {
        router.route("/graphql").handler(context -> {
            HttpServerResponse response = context.response();
            response.putHeader("content-type", "text/plain");
            response.end("Hello World from GraphQL!");
        });

        GraphiQLHandlerOptions options = new GraphiQLHandlerOptions().setEnabled(true);
        router.route("/graphiql/*").handler(GraphiQLHandler.create(options));
    }
}
