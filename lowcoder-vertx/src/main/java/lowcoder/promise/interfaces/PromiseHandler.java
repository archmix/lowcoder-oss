package lowcoder.promise.interfaces;

import io.vertx.core.AsyncResult;
import io.vertx.core.Handler;
import io.vertx.core.Promise;

@FunctionalInterface
public interface PromiseHandler<E> extends Handler<AsyncResult<E>> {

    public static void handle(Promise<?> promise, AsyncResult<?> result) {
        if(result.succeeded()){
            promise.complete();
            return;
        }
        promise.fail(result.cause());
    }
}
