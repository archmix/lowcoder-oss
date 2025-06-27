package lowcoder.metadata.infra;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.SQLCache;

import java.util.stream.Stream;

public class TableCache {
  private static final TableCache INSTANCE = new TableCache();

  private final SQLCache sqlCache = SQLCache.of();
  private final LocalMap<String, Table> cache;

  private TableCache(){
    this.cache = Vertx.currentContext().owner().sharedData().getLocalMap(TableCache.class.getName());
  }

  public static TableCache of() {
    return INSTANCE;
  }

  public void add(Table table) {
    this.cache.put(table.getName(), table);
    sqlCache.init(table);
  }

  public Table get(String tableName){
    return this.cache.get(tableName);
  }

  public Stream<Table> stream(){
    return this.cache.values().stream();
  }
}
