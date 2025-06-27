package lowcoder.sql.interfaces;

import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "of")
public class PagedData {
  private final long total;
  private final long limit;
  private final long offset;
  private final JsonArray data;

  public String encode() {
    var json = new JsonObject();
    json.put("total", this.total);
    json.put("limit", this.limit);
    json.put("offset", this.offset);
    json.put("data", this.data);
    return json.encode();
  }
}
