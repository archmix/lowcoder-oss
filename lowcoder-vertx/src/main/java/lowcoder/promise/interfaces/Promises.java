package lowcoder.promise.interfaces;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import io.vertx.core.CompositeFuture;
import io.vertx.core.Future;
import io.vertx.core.impl.future.CompositeFutureImpl;

public class Promises {
  private final List<FuturePromise<?>> promises;

  private Promises() {
    this.promises = new ArrayList<>();
  }

  public CompositeFuture all() {
    return Promises.all(promises);
  }

  public CompositeFuture any() {
    return Promises.any(promises);
  }

  public CompositeFuture join() {
    return Promises.join(promises);
  }

  public <T> FuturePromise<T> add() {
    FuturePromise<T> promise = FuturePromise.promise();
    this.promises.add(promise);
    return promise;
  }

  public int size() {
    return this.promises.size();
  }

  public boolean isEmpty() {
    return this.size() == 0;
  }

  public static Promises promises() {
    return new Promises();
  }

  public static CompositeFuture all(FuturePromise<?>... promises) {
    return all(collection(promises));
  }

  public static CompositeFuture any(FuturePromise<?>... promises) {
    return any(collection(promises));
  }

  public static CompositeFuture join(FuturePromise<?>... promises) {
    return join(collection(promises));
  }

  private static Collection<FuturePromise<?>> collection(FuturePromise<?>... promises) {
    if (promises == null || promises.length == 0) {
      throw new IllegalArgumentException(
          "In order to composite promises, you need to pass at least one instance of Promise");
    }
    return Arrays.asList(promises);
  }

  public static CompositeFuture all(Collection<FuturePromise<?>> promises) {
    return CompositeFutureImpl.all(futureArray(promises));
  }

  public static CompositeFuture any(Collection<FuturePromise<?>> promises) {
    return CompositeFutureImpl.any(futureArray(promises));
  }

  public static CompositeFuture join(Collection<FuturePromise<?>> promises) {
    return CompositeFutureImpl.join(futureArray(promises));
  }

  private static Future<?>[] futureArray(Collection<FuturePromise<?>> promises) {
    return promises.stream().map(promise -> promise.asFuture()).toArray(size -> new Future[size]);
  }
}
