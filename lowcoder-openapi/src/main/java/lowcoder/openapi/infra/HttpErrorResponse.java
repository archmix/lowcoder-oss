package lowcoder.openapi.infra;

import com.google.common.net.MediaType;
import io.vertx.core.http.HttpServerResponse;
import io.vertx.core.json.JsonObject;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

@Slf4j
class HttpErrorResponse {

  public static void handleError(HttpServerResponse response, UUID requestId, Throwable error) {
    log.atError().setMessage(error.getMessage()).setCause(error).addKeyValue("requestId", requestId.toString()).log();

    JsonObject responseBody = JsonObject.of();
    responseBody.put("requestId", requestId.toString());
    responseBody.put("message", error.getMessage());

    response
      .setStatusCode(500)
      .putHeader("Content-type", MediaType.JSON_UTF_8.toString())
      .end(responseBody.toBuffer());
  }
}
