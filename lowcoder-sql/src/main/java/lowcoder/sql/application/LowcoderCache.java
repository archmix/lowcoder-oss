package lowcoder.sql.application;

import lowcoder.sql.infra.InsertSqlHandler;
import morphos.api.interfaces.MorphosCache;
import morphos.api.interfaces.Table;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Stream;

public class LowcoderCache {
  private static final LowcoderCache CACHE = new LowcoderCache();
  private final Map<String, InsertSqlHandler> insertHandlers = new ConcurrentHashMap<>();
  private MorphosCache cache;

  public static LowcoderCache create(){
    return CACHE;
  }

  public void init(MorphosCache cache){
    this.cache = cache;
  }

  public Optional<Table> getTable(String tableName) {
    return this.cache.getTable(tableName);
  }

  public InsertSqlHandler getInsertSqlHandler(Table table) {
    var insertHandler = this.insertHandlers.get(table.getName());
    if(insertHandler == null) {
      insertHandler = InsertSqlHandler.of(table);
      this.insertHandlers.put(table.getName(), insertHandler);
    }
    return insertHandler;
  }

  public Stream<Table> tables() {
    return this.cache.tables().stream();
  }
}
