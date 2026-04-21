package lowcoder.core.interfaces;

import io.vertx.ext.web.Router;
import lowcoder.api.infra.HttpEndpointURIBuilder;
import lowcoder.sql.infra.ConnectionPool;
import morphos.api.interfaces.Field;
import morphos.api.interfaces.Table;

public interface HttpHandlerService {
  void accept(Router router, ConnectionPool pool, Table table);

  default String uri(Table table) {
    return HttpEndpointURIBuilder.create().path(table.getName()).pathParam(table.getPrimaryKeys().stream()
      .map(Field::getName).toArray(String[]::new)).build();
  }
}
