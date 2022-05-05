package lowcoder.openapi.infra;

import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lowcoder.core.interfaces.RouterService;
import lowcoder.core.interfaces.RouterServiceSpecification;

@RouterServiceSpecification
public class OpenAPIRouter implements RouterService {

    @Override
    public void accept(Router router) {
        router.route("/api").handler(context -> {
            HttpServerResponse response = context.response();
            response.putHeader("content-type", "text/plain");
            response.end("Hello World from Open API!");
        });
    }
}
