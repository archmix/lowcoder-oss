package lowcoder.sql.interfaces;

import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Column;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
class TupleBuilder {
  private final Tuple tuple;

  public static TupleBuilder of() {
    return new TupleBuilder(Tuple.tuple());
  }

  public TupleBuilder add(Column column, Object value) {
    TupleValue.setValue(this.tuple, column, value);
    return this;
  }

  public Tuple build() {
    return tuple;
  }
}
