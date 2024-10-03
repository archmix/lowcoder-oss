package lowcoder.sql.interfaces;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Column;
import lowcoder.metadata.interfaces.PrimaryKey;
import lowcoder.metadata.interfaces.Table;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class UpdateCommand {
  private final SQLGenerator sqlGenerator = SQLGenerator.create();
  private final JDBCPool pool;
  private final Table table;

  public void execute(JsonObject json, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    Collection<Column> columns = table.getColumns().stream().filter(column -> json.containsKey(column.getName())).collect(Collectors.toList());
    Collection<PrimaryKey> pkColumns = table.getPrimaryKeys();

    List<String> columnNames = columns.stream().map(Column::getName).collect(Collectors.toList());

    List<String> pkColumnNames = pkColumns.stream().map(pk -> pk.getColumn().getName()).collect(Collectors.toList());

    String sql = this.sqlGenerator.updateSQL(table.getName(), columnNames, pkColumnNames);
    log.debug("SQL generated for table {} is {}", table.getName(), sql);

    Tuple values = Tuple.tuple();
    columns.forEach(column -> {
      column.getType().setValue(values, json.getValue(column.getName()));
    });
    pkColumns.forEach(pk -> {
      pk.getColumn().getType().setValue(values, json.getValue(pk.getColumn().getName()));
    });

    this.pool.preparedQuery(sql).execute(values).onSuccess(rows ->{
      if(rows.rowCount() <= 0) {
        onFail.handle(new RuntimeException("Expected 1 row to be updated, but got " + rows.size()));
        return;
      }
      onSuccess.handle(null);
    }).onFailure(onFail);
  }
}
