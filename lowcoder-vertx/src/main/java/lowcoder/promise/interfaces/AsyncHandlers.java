package lowcoder.promise.interfaces;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AsyncHandlers {
  private static final Logger LOGGER = LoggerFactory.getLogger(AsyncHandlers.class);

  public static Handler<Throwable> exceptionHandler(){
    return new Handler<Throwable>() {
      @Override
      public void handle(Throwable cause) {
        LOGGER.error(cause.getMessage(), cause);
      }
    };
  }

  public static <T> void handle(AsyncResult<T> result, PromiseHandler<T> onSuccess) {
    AsyncHandlers.handle(result, onSuccess, onFail ->{});
  }

  public static <T> void handle(AsyncResult<T> result, PromiseHandler<T> onSuccess, PromiseHandler<T> onFail) {
    if(result.failed()){
      Throwable cause = result.cause();
      LOGGER.error(cause.getMessage(), cause);
      onFail.handle(result);
      return;
    }
    onSuccess.handle(result);
  }
}
