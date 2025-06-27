package lowcoder.api.infra;

import io.vertx.core.Handler;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class ExceptionHandler implements Handler<Throwable> {
  private static final ExceptionHandler INSTANCE = new ExceptionHandler();

  public static ExceptionHandler get() {
    return INSTANCE;
  }

  @Override
  public void handle(Throwable throwable) {
    log.error(throwable.getMessage(), throwable);
  }
}
