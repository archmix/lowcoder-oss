package lowcoder.sql.infra;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Field;

import java.util.Collection;

@RequiredArgsConstructor(staticName = "create")
class TupleBuilder {
  private final Collection<Field> fields;

  public Tuple build(JsonObject json) {
    final var tuple = Tuple.tuple();
    fields.forEach(field -> {
      TypeAdapter.valueOf(field).set(tuple, json.getValue(field.getName()));
    });
    return tuple;
  }


}
