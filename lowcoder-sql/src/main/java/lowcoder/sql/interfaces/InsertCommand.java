package lowcoder.sql.interfaces;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import morphos.api.interfaces.Column;
import morphos.api.interfaces.Table;
import lowcoder.sql.infra.SQLCache;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class InsertCommand extends ColumnCommand {
  private final SQLCache sqlCache = SQLCache.of();
  private final JDBCPool pool;
  private final Table table;

  public void execute(JsonObject json, Handler<Void> onSuccess, Handler<Throwable> onFail) {
    var columns = columnsForInsert(table);
    String sql = this.toSQL(columns);
    log.debug("Insert SQL generated for table {} is {}", table.getName(), sql);

    this.pool.preparedQuery(sql).execute(values(json, columns)).onSuccess(rows ->{
      onSuccess.handle(null);
    }).onFailure(onFail);
  }

  public void executeAndGetGeneratedKeys(JsonObject json, Handler<JsonObject> onSuccess, Handler<Throwable> onFail) {
    var columns = columnsForInsert(table);
    String sql = toSQL(columns);

    this.pool.preparedQuery(sql).execute(values(json, columns)).onSuccess(rows ->{
      JsonObject ids = new JsonObject();
      Row generatedIds = rows.property(JDBCPool.GENERATED_KEYS);
      if(generatedIds.size() == 0) {
        onSuccess.handle(ids);
        return;
      }

      var index = 0;
      for(var pk : table.getPrimaryKeys())  {
        if(!pk.getGenerated()){
          continue;
        }
        ids.put(pk.getName(), generatedIds.getValue(index++));
      }
      onSuccess.handle(ids);
    }).onFailure(onFail);
  }

  private String toSQL(Collection<Column> columns) {
    var sql = this.sqlCache.get(table, SQLCache.Command.UPDATE);
    if(sql != null) {
      return sql;
    }

    List<String> columnNames = columns.stream().map(Column::getName).collect(Collectors.toList());
    var collector = Collectors.joining(",");

    String insertTemplate = "INSERT INTO {0}({1}) VALUES ({2})";
    String columnsNames = columnNames.stream().collect(collector);
    String columnsValues = columnNames.stream().map(column -> "?").collect(collector);

    String generatedSQL = MessageFormat.format(insertTemplate, table.getName(), columnsNames, columnsValues);
    this.sqlCache.add(table, SQLCache.Command.INSERT, generatedSQL);

    return generatedSQL;
  }

  private Tuple values(JsonObject json, Collection<Column> columns) {
    var values = TupleBuilder.of();
    columns.forEach(column -> values.add(column, json.getValue(column.getName())));
    return values.build();
  }
}
