package lowcoder.core.interfaces;

import io.vertx.core.Vertx;
import lombok.RequiredArgsConstructor;
import lowcoder.config.interfaces.Config;
import lowcoder.config.interfaces.ConfigBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RequiredArgsConstructor(staticName = "create")
public class LowcoderContainer {
    private static final Logger log = LoggerFactory.getLogger(LowcoderContainer.class);

    private final LowcoderCommandLine cmd;

    public void start() {
        Vertx vertx = Vertx.vertx();

        ConfigBuilder.create().withFile(this.cmd.getConfigFile()).withEnvironmentVariables().build(vertx, handler -> {
            if(handler.failed()){
                Throwable cause = handler.cause();
                log.error(cause.getMessage(), cause);
                vertx.close();
                return;
            }
            Config config = handler.result();
            vertx.deployVerticle(new HttpServerVerticle(config));
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() ->{
            vertx.close();
        }));
    }
}