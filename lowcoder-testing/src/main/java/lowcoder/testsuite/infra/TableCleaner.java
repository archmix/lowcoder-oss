package lowcoder.testsuite.infra;

import io.vertx.core.Vertx;
import io.vertx.jdbcclient.JDBCPool;
import lombok.extern.slf4j.Slf4j;
import lowcoder.api.interfaces.JDBCPoolConsumer;
import lowcoder.api.interfaces.JDBCPoolConsumerSpecification;

import java.util.List;

import static java.text.MessageFormat.*;

@JDBCPoolConsumerSpecification
@Slf4j
public class TableCleaner implements JDBCPoolConsumer {
  private static final TableCleaner instance = new TableCleaner();

  private JDBCPool pool;

  public TableCleaner() {}

  public static TableCleaner create() {
    return instance;
  }

  @Override
  public void accept(JDBCPool pool) {
    instance.pool = pool;
  }

  public void clean(Vertx vertx, String... tableNames) {
    var sqlTemplate = "DELETE FROM {0}";

    log.info("Cleaning tables {}", tableNames);
    List.of(tableNames).forEach(tableName -> {
      var sql = format(sqlTemplate, tableName);
      instance.pool.query(sql).execute().onFailure(e -> log.error("Error executing sql {}", sql, e));
    });
  }
}
