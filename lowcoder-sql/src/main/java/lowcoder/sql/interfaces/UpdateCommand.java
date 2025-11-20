package lowcoder.sql.interfaces;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morphos.api.interfaces.Column;
import morphos.api.interfaces.Table;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class UpdateCommand extends ColumnCommand {
  private final JDBCPool pool;
  private final Table table;

  public void execute(JsonObject json, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    var columns = columnsForUpdate(table).stream().filter(column -> json.containsKey(column.getName())).collect(Collectors.toList());

    String sql = this.toSQL(columns);
    log.debug("SQL generated for table {} is {}", table.getName(), sql);

    TupleBuilder values = TupleBuilder.of();
    columns.forEach(column -> {
      values.add(column, json.getValue(column.getName()));
    });
    table.getPrimaryKeys().forEach(pk -> {
      values.add(pk.getColumn(), json.getValue(pk.getName()));
    });

    this.pool.preparedQuery(sql).execute(values.build()).onSuccess(rows ->{
      if(rows.rowCount() <= 0) {
        onFail.handle(new RuntimeException("Expected 1 row to be updated, but got " + rows.size()));
        return;
      }
      onSuccess.handle(null);
    }).onFailure(onFail);
  }

  private String toSQL(Collection<Column> columns) {
    var comma_collector = Collectors.joining(",");

    String updateTemplate = "UPDATE {0} SET {1} WHERE 1 = 1 {2}";
    String columnSet = columns.stream().map(SetExpression.of()).collect(comma_collector);

    var space_joining = Collectors.joining(" ");
    var whereClause = table.getPrimaryKeys().stream().map(AndClause.of()).collect(space_joining);

    return MessageFormat.format(updateTemplate, table.getName(), columnSet, whereClause);
  }
}
