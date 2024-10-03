package lowcoder.sql.interfaces;

import io.vertx.core.Handler;
import io.vertx.core.json.JsonObject;
import io.vertx.jdbcclient.JDBCPool;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.metadata.interfaces.Table;

import java.rmi.NoSuchObjectException;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
@Slf4j
public class SelectByIdCommand {
  private final SQLGenerator sqlGenerator = SQLGenerator.create();
  private final JDBCPool pool;
  private final Table table;

  public void execute(SearchOptions options, Handler<JsonObject> onSuccess, Handler<Throwable> onFail) {
    Collection<String> constraints = this.table.getPrimaryKeys().stream().map(pk -> pk.getColumn().getName()).collect(Collectors.toList());

    Collection<String> fields = options.getFields(this.table);
    String sql = sqlGenerator.selectSQL(table.getName(), fields, constraints);

    Tuple tuple = Tuple.tuple();
    table.getPrimaryKeys().forEach(pk ->{
      pk.getColumn().getType().setValue(tuple, options.getFilter().getValue(pk.getColumn().getName()));
    });

    pool.preparedQuery(sql).execute(tuple).onSuccess(rows ->{
      if(rows.size() != 1) {
        log.error("No data found for provided id {}", options.getFilter().encode());
        onFail.handle(new NoSuchObjectException("No data found for provided id"));
        return;
      }

      JsonObject result = new JsonObject();
      rows.forEach(row -> {
        fields.forEach(field -> {
          result.put(field, row.getValue(field));
        });
      });

      onSuccess.handle(result);
    }).onFailure(onFail);
  }
}
