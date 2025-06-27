package lowcoder.promise.interfaces;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AsyncHandlers {
  public static Handler<Throwable> exceptionHandler(){
    return new Handler<Throwable>() {
      @Override
      public void handle(Throwable cause) {
        log.error(cause.getMessage(), cause);
      }
    };
  }

  public static <T> void handle(AsyncResult<T> result, PromiseHandler<T> onSuccess) {
    AsyncHandlers.handle(result, onSuccess, onFail ->{});
  }

  public static <T> void handle(AsyncResult<T> result, PromiseHandler<T> onSuccess, PromiseHandler<T> onFail) {
    if(result.failed()){
      Throwable cause = result.cause();
      log.error(cause.getMessage(), cause);
      onFail.handle(result);
      return;
    }
    onSuccess.handle(result);
  }
}
