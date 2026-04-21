package lowcoder.sql.infra;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lowcoder.sql.interfaces.AndClause;
import lowcoder.sql.interfaces.SetExpression;
import morphos.api.interfaces.Field;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.function.Function;
import java.util.stream.Collector;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class UpdateSqlHandler {
  private static final Collector<CharSequence, ?, String> COMMA_COLLECTOR = Collectors.joining(",");
  private static final Function<Field, Boolean> NOT_PRIMARY_KEY = field -> !(field instanceof PrimaryKey);
  private final String template;
  private final Table table;

  public static UpdateSqlHandler of(Table table) {
    String updateTemplate = "UPDATE {0} SET {1} WHERE 1 = 1 {2}";

    var space_joining = Collectors.joining(" ");
    var whereClause = table.getPrimaryKeys().stream().map(AndClause.of()).collect(space_joining);

    var sqlTemplate = MessageFormat.format(updateTemplate, table.getName(), "{0}", whereClause);
    return new UpdateSqlHandler(sqlTemplate, table);
  }

  public String getSql(Collection<String> fieldNames) {
    var columns = this.columns(fieldNames);
    String columnSet = columns.stream().map(SetExpression.of()).collect(COMMA_COLLECTOR);

    return MessageFormat.format(this.template, columnSet);
  }

  public Tuple values(JsonObject json) {
    var columns = columns(json.fieldNames());
    columns.addAll(this.table.getPrimaryKeys());

    TupleBuilder values = TupleBuilder.create(columns);
    return values.build(json);
  }

  private Collection<Field> columns(Collection<String> fieldNames) {
    if(fieldNames.isEmpty()) {
      return this.table.getFields().stream().filter(NOT_PRIMARY_KEY::apply).collect(Collectors.toList());
    }

    return this.table.getFields().stream()
      .filter(field -> fieldNames.contains(field.getName()) && NOT_PRIMARY_KEY.apply(field))
      .collect(Collectors.toList());
  }
}
