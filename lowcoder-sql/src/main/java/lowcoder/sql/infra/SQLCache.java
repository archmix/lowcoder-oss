package lowcoder.sql.infra;

import io.vertx.core.Vertx;
import io.vertx.core.shareddata.LocalMap;
import lowcoder.metadata.interfaces.Table;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class SQLCache {
  private static final SQLCache INSTANCE = new SQLCache();

  private final LocalMap<String, Map<Command, String>> cache;

  private SQLCache(){
    this.cache = Vertx.currentContext().owner().sharedData().getLocalMap(SQLCache.class.getName());
  }

  public static SQLCache of() {
    return INSTANCE;
  }

  public static enum Command {
    SELECT, INSERT, UPDATE, DELETE;
  }

  public void init(Table table) {
    this.cache.put(table.getName(), new ConcurrentHashMap<>());
  }

  public void add(Table table, Command command, String sql){
    this.cache.get(table.getName()).put(command, sql);
  }

  public String get(Table table, Command command) {
    return this.cache.get(table.getName()).get(command);
  }
}
