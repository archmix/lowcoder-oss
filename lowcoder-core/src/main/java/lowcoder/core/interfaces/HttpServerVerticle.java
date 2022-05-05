package lowcoder.core.interfaces;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Promise;
import io.vertx.core.http.HttpServer;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.ext.web.Router;
import lowcoder.config.interfaces.Config;
import lowcoder.core.infra.ConfigEntries;
import lowcoder.promise.interfaces.PromiseHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ServiceLoader;

class HttpServerVerticle extends AbstractVerticle {
    private HttpServer server;

    private final int httpPort;

    private static final Logger log = LoggerFactory.getLogger(HttpServerVerticle.class);

    HttpServerVerticle(Config config){
        this.httpPort = config.getNumber(ConfigEntries.HttpPort.create()).intValue();
    }

    @Override
    public void start(Promise<Void> startPromise) throws Exception {
        Router router = Router.router(vertx);
        router.route("/").handler(ctx -> {
            HttpServerResponse response = ctx.response();
            response.putHeader("content-type", "text/plain");
            response.end("Hello World from Vert.x-Web!");
        });

        ServiceLoader.load(RouterService.class).forEach(service -> {
            log.info("Registering router service {}", service.getClass().getName());
            service.accept(router);
        });

        this.server = this.vertx.createHttpServer();
        this.server.requestHandler(router).listen(this.httpPort, result -> PromiseHandler.handle(startPromise, result));
    }

    @Override
    public void stop(Promise<Void> stopPromise) throws Exception {
        this.server.close(result -> PromiseHandler.handle(stopPromise, result));
        this.server = null;
    }
}
