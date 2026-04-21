package lowcoder.openapi.interfaces;

import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.RoutingContext;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.openapi.infra.MimeType;

import static lowcoder.core.application.LowcoderContainerService.*;

@Slf4j
@NoArgsConstructor(access = lombok.AccessLevel.PRIVATE)
class HttpErrorResponse {

  public static void handleError(RoutingContext context, Throwable error) {
    String requestId = context.get(X_REQUEST_ID);

    log.error(error.getMessage(), error);

    JsonObject responseBody = new JsonObject();
    responseBody.put("requestId", requestId);
    responseBody.put("message", error.getMessage());

    context.response()
      .setStatusCode(500)
      .putHeader("Content-type", MimeType.JSON)
      .end(responseBody.toBuffer());
  }
}
