package lowcoder.testsuite.infra;

import io.vertx.core.MultiMap;
import io.vertx.core.http.HttpServerRequest;
import io.vertx.ext.web.RoutingContext;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;

import static org.mockito.Mockito.*;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class RoutingContextMockBuilder {
  private final HttpServerRequest request;
  private final RoutingContext context;
  private final MultiMap params;

  public static RoutingContextMockBuilder create() {
    return new RoutingContextMockBuilder(mock(HttpServerRequest.class), mock(RoutingContext.class),
      MultiMap.caseInsensitiveMultiMap());
  }

  public RoutingContextMockBuilder withParams(String key, String value){
    params.add(key, value);
    return this;
  }

  public RoutingContext build(){
    when(request.params()).thenReturn(params);
    when(context.request()).thenReturn(request);
    return context;
  }
}