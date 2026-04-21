package lowcoder.sql.interfaces;

import io.vertx.sqlclient.Tuple;

public enum PaginationType {
  LIMIT_OFFSET{
    @Override
    public String getStatement() {
      return " LIMIT ? OFFSET ?";
    }

    @Override
    public void setTuple(Tuple tuple, Long offset, Long limit) {
      tuple.addLong(limit);
      tuple.addLong(offset);
    }
  },
  OFFSET_FETCH{
    @Override
    public String getStatement() {
      return " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    }

    @Override
    public void setTuple(Tuple tuple, Long offset, Long limit) {
      tuple.addLong(offset);
      tuple.addLong(limit);
    }
  };

  public static PaginationType from(String url) {
    if(url.contains("sqlserver") || url.contains("oracle") || url.contains("mssql")) {
      return OFFSET_FETCH;
    }
    return LIMIT_OFFSET;
  }

  public abstract String getStatement();

  public abstract void setTuple(Tuple tuple, Long offset, Long limit);
}
