package lowcoder.openapi.interfaces;

import lombok.RequiredArgsConstructor;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.ConnectionPool;

@RequiredArgsConstructor
public abstract class AbstractHttpHandler implements HttpHandler {
  protected final ConnectionPool pool;
  protected final Table table;
}
