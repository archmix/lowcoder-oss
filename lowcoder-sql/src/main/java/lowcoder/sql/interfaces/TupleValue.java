package lowcoder.sql.interfaces;

import io.vertx.sqlclient.Tuple;
import morphos.api.interfaces.Column;

class TupleValue {
  public static void setValue(Tuple tuple, Column column, Object value) {
    TupleType.valueOf(column.getType()).ifPresent(tupleType -> tupleType.setValue(tuple, value));
  }
}
