package lowcoder.sql.infra;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Row;
import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Field;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class InsertSqlHandler {
  @Getter
  private final String sql;
  private final TupleBuilder tupleBuilder;
  private final Collection<PrimaryKey> pks;

  public static InsertSqlHandler of(Table table) {
    var pks = table.getPrimaryKeys().stream().filter(field -> !field.getGenerated()).map(PrimaryKey::getColumn)
      .collect(Collectors.toList());

    var columns = new ArrayList<Field>(table.getColumns());
    columns.addAll(table.getForeignKeys().stream().map(ForeignKey::getColumn).collect(Collectors.toList()));
    columns.addAll(pks);

    var tupleBuilder = TupleBuilder.create(columns);
    var columnNames = columns.stream().map(Field::getName).collect(Collectors.toList());
    var collector = Collectors.joining(",");

    var insertTemplate = "INSERT INTO {0}({1}) VALUES ({2})";
    var columnsNames = columnNames.stream().collect(collector);
    var columnsValues = columnNames.stream().map(column -> "?").collect(collector);

    var insertSql = MessageFormat.format(insertTemplate, table.getName(), columnsNames, columnsValues);
    var generatedPks = table.getPrimaryKeys().stream().filter(PrimaryKey::getGenerated).collect(Collectors.toList());

    return new InsertSqlHandler(insertSql, tupleBuilder, generatedPks);
  }

  public JsonObject getGeneratedKeys(Row row) {
    var json = new JsonObject();

    if(row.size() == 0) {
      return json;
    }

    var index = 0;
    for(var pk : this.pks){
      json.put(pk.getName(), row.getValue(index++));
    };

    return json;
  }

  public Tuple values(JsonObject json) {
    return this.tupleBuilder.build(json);
  }
}
