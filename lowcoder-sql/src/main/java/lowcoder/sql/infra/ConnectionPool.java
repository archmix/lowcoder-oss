package lowcoder.sql.infra;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.RequiredArgsConstructor;
import lowcoder.api.infra.ConfigEntries;
import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.interfaces.InsertCommand;
import lowcoder.sql.interfaces.SelectByIdCommand;
import lowcoder.sql.interfaces.UpdateCommand;

@RequiredArgsConstructor
public class ConnectionPool {
  private final JDBCPool pool;

  public static ConnectionPool create(Vertx vertx, JsonObject config) {
    JDBCPool pool = JDBCPool.pool(vertx,
      new JDBCConnectOptions()
        .setJdbcUrl(ConfigEntries.Database.getUrl(config))
        .setUser(ConfigEntries.Database.getUser(config))
        .setPassword(ConfigEntries.Database.getPassword(config)),
      new PoolOptions()
        .setMaxSize(ConfigEntries.ConnectionPool.getMaxSize(config))
        .setName("lowcoder-pool")
    );

    return new ConnectionPool(pool);
  }

  public InsertCommand insertCommand(Table table) {
    return InsertCommand.create(this.pool, table);
  }

  public UpdateCommand updateCommand(Table table) {
    return UpdateCommand.create(this.pool, table);
  }

  public SelectByIdCommand selectByIdCommand(Table table) {
    return SelectByIdCommand.create(this.pool, table);
  }

  public void close() {
    this.pool.close();
  }
}
