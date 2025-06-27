package lowcoder.sql.infra;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.ConfigEntries;
import lowcoder.api.interfaces.JDBCPoolConsumer;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.interfaces.InsertCommand;
import lowcoder.sql.interfaces.SelectCommand;
import lowcoder.sql.interfaces.UpdateCommand;

import java.util.ServiceLoader;

@RequiredArgsConstructor
@Slf4j
public class ConnectionPool {
  @Getter
  private final JDBCPool pool;
  private final PaginationType paginationType;

  public static ConnectionPool create(Vertx vertx, JsonObject config) {
    var pool = JDBCPool.pool(vertx,
      new JDBCConnectOptions()
        .setJdbcUrl(ConfigEntries.Database.getUrl(config))
        .setUser(ConfigEntries.Database.getUser(config))
        .setPassword(ConfigEntries.Database.getPassword(config)),
      new PoolOptions()
        .setMaxSize(ConfigEntries.ConnectionPool.getMaxSize(config))
        .setName("lowcoder-pool")
    );

    ServiceLoader.load(JDBCPoolConsumer.class).forEach(visitor -> visitor.accept(pool));

    return new ConnectionPool(pool, PaginationType.from(ConfigEntries.Database.getUrl(config)));
  }

  public InsertCommand insertCommand(Table table) {
    return InsertCommand.create(this.pool, table);
  }

  public UpdateCommand updateCommand(Table table) {
    return UpdateCommand.create(this.pool, table);
  }

  public SelectCommand selectCommand() {
    return SelectCommand.create(this.pool, this.paginationType);
  }

  public void close() {
    this.pool.close();
  }
}
