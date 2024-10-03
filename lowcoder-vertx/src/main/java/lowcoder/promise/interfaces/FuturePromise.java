package lowcoder.promise.interfaces;

import io.vertx.core.AsyncResult;
import io.vertx.core.Future;
import io.vertx.core.Promise;

public class FuturePromise<T> {

  public static <T> AsyncResult<T> succeeded() {
    return Future.succeededFuture();
  }

  public static <T> AsyncResult<T> succeeded(final T result) {
    return Future.succeededFuture(result);
  }

  public static <T> AsyncResult<T> failed(final Throwable cause) {
    return Future.failedFuture(cause);
  }

  public static <T> AsyncResult<T> failed(final String failureMessage) {
    return Future.failedFuture(failureMessage);
  }

  public static <T> FuturePromise<T> promise() {
    return new FuturePromise<T>();
  }

  private final Promise<T> promise;

  public FuturePromise() {
    this.promise = Promise.promise();
  }

  public final boolean isComplete() {
    return this.asFuture().isComplete();
  }

  public final void complete() {
    this.promise.complete();
  }

  public final void complete(final T result) {
    this.promise.complete(result);
  }

  public final void fail(final String message) {
    this.promise.fail(message);
  }

  public final void fail(final Throwable cause) {
    this.promise.fail(cause);
  }

  public final Promise<T> asPromise() {
    return this.promise;
  }

  public final Future<T> asFuture(){
    return this.promise.future();
  }

  public final <U> Future<U> compose(final FuturePromise<U> other) {
    return asFuture().compose(mapper -> other.asFuture());
  }
}
