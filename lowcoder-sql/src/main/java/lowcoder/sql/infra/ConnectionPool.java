package lowcoder.sql.infra;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.LowcoderConfig;
import lowcoder.api.interfaces.JDBCPoolConsumer;
import morphos.api.interfaces.Table;
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
        .setJdbcUrl(LowcoderConfig.Database.getUrl(config))
        .setUser(LowcoderConfig.Database.getUser(config))
        .setPassword(LowcoderConfig.Database.getPassword(config)),
      new PoolOptions()
        .setMaxSize(LowcoderConfig.ConnectionPool.getMaxSize(config))
        .setName("lowcoder-pool")
    );

    ServiceLoader.load(JDBCPoolConsumer.class).forEach(visitor -> visitor.accept(pool));

    return new ConnectionPool(pool, PaginationType.from(LowcoderConfig.Database.getUrl(config)));
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
