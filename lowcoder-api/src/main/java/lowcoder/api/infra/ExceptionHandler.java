package lowcoder.api.infra;

import io.vertx.core.Handler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExceptionHandler implements Handler<Throwable> {
  private static final Logger LOGGER = LoggerFactory.getLogger(ExceptionHandler.class);
  private static final ExceptionHandler INSTANCE = new ExceptionHandler();

  public static ExceptionHandler get() {
    return INSTANCE;
  }

  @Override
  public void handle(Throwable throwable) {
    LOGGER.error(throwable.getMessage(), throwable);
  }
}
