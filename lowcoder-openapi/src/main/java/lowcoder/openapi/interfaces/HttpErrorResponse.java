package lowcoder.openapi.interfaces;

import com.google.common.net.MediaType;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

import static lowcoder.core.application.LowcoderStarter.*;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class HttpErrorResponse {

  public static void handleError(RoutingContext context, Throwable error) {
    String requestId = context.get(X_REQUEST_ID);

    log.error(error.getMessage(), error);

    JsonObject responseBody = JsonObject.of();
    responseBody.put("requestId", requestId);
    responseBody.put("message", error.getMessage());

    context.response()
      .setStatusCode(500)
      .putHeader("Content-type", MediaType.JSON_UTF_8.toString())
      .end(responseBody.toBuffer());
  }
}
