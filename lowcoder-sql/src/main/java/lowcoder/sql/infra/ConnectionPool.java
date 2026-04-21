package lowcoder.sql.infra;

import io.vertx.core.Vertx;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCConnectOptions;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.PoolOptions;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.infra.LowcoderConfiguration;
import lowcoder.api.interfaces.JDBCPoolConsumer;
import lowcoder.sql.application.InsertCommand;
import lowcoder.sql.application.SelectCommand;
import lowcoder.sql.application.UpdateCommand;
import lowcoder.sql.interfaces.PaginationType;

import java.util.ServiceLoader;

import static lowcoder.api.infra.LowcoderConfiguration.ConnectionPoolEntries.*;
import static lowcoder.api.infra.LowcoderConfiguration.DatabaseEntries.*;

@RequiredArgsConstructor
@Slf4j
public class ConnectionPool {
  @Getter
  private final JDBCPool pool;
  private final PaginationType paginationType;

  public static ConnectionPool create(Vertx vertx, JsonObject config) {
    var pool = JDBCPool.pool(vertx,
      new JDBCConnectOptions()
        .setJdbcUrl(URL.get(config))
        .setUser(USER.get(config))
        .setPassword(PASSWORD.get(config)),
      new PoolOptions()
        .setMaxSize(MAX_SIZE.get(config))
        .setName("lowcoder-pool")
    );

    ServiceLoader.load(JDBCPoolConsumer.class).forEach(visitor -> visitor.accept(pool));

    return new ConnectionPool(pool, PaginationType.from(URL.get(config)));
  }

  public InsertCommand insertCommand(InsertSqlHandler handler) {
    return InsertCommand.create(this.pool, handler);
  }

  public UpdateCommand updateCommand(UpdateSqlHandler handler) {
    return UpdateCommand.create(this.pool, handler);
  }

  public SelectCommand selectCommand() {
    return SelectCommand.create(this.pool, this.paginationType);
  }

  public void close() {
    this.pool.close();
  }
}
