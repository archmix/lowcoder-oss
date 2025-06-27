package lowcoder.sql.infra;

import io.vertx.sqlclient.Tuple;

public enum PaginationType {
  LIMIT_OFFSET{
    @Override
    public String getStatement() {
      return " LIMIT ? OFFSET ?";
    }

    @Override
    public void setTuple(Tuple tuple, int offset, int limit) {
      tuple.addInteger(limit);
      tuple.addInteger(offset);
    }
  },
  OFFSET_FETCH{
    @Override
    public String getStatement() {
      return " OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
    }

    @Override
    public void setTuple(Tuple tuple, int offset, int limit) {
      tuple.addInteger(offset);
      tuple.addInteger(limit);
    }
  };

  public static PaginationType from(String url) {
    if(url.contains("sqlserver") || url.contains("oracle") || url.contains("mssql")) {
      return OFFSET_FETCH;
    }
    return LIMIT_OFFSET;
  }

  public abstract String getStatement();

  public abstract void setTuple(Tuple tuple, int offset, int limit);
}
