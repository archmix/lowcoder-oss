package lowcoder.api.application;

import io.vertx.core.AbstractVerticle;
import io.vertx.core.Context;
import io.vertx.core.Promise;
import io.vertx.core.Vertx;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.interfaces.ContainerService;
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.promise.interfaces.Promises;

import java.util.ServiceLoader;
import java.util.function.BiConsumer;

@Slf4j
public class LowcoderContainer extends AbstractVerticle {
  private final ServiceLoader<ContainerService> serviceLoader;

  LowcoderContainer() {
    this.serviceLoader = ServiceLoader.load(ContainerService.class);
  }

  @Override
  public void init(Vertx vertx, Context context) {
    super.init(vertx, context);
    serviceLoader.forEach(service -> {
      service.init(vertx, context);
    });
  }

  @Override
  public void start(Promise<Void> startPromise) throws Exception {
    applyEvent(ContainerService::start, startPromise);
  }

  @Override
  public void stop(Promise<Void> stopPromise) throws Exception {
    applyEvent(ContainerService::stop, stopPromise);
  }

  private void applyEvent(BiConsumer<ContainerService, FuturePromise<Void>> event, Promise<Void> eventPromise){
    Promises promises = Promises.promises();

    serviceLoader.forEach(service -> {
      event.accept(service, promises.add());
    });

    promises.all().onFailure(eventPromise::fail).onSuccess(result -> {
      eventPromise.complete();
    });
  }
}
