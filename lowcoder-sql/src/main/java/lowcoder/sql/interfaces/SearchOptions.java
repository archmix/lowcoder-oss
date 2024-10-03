package lowcoder.sql.interfaces;

import io.vertx.core.json.JsonObject;
import io.vertx.sqlclient.Tuple;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lowcoder.metadata.interfaces.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

@RequiredArgsConstructor(staticName = "create")
public class SearchOptions {
  private final Collection<String> fields = new ArrayList<>();
  private final Collection<String> expand = new ArrayList<>();
  private final Collection<Sorting> sorting = new ArrayList<>();
  @Getter
  private final Pagination pagination = new Pagination();
  @Getter
  private final JsonObject filter = new JsonObject();

  public Collection<String> getFields(Table table) {
    if(!this.fields.isEmpty()) {
      return fields;
    }
    Collection<String> fields = new ArrayList<>();
    fields.addAll(table.getColumns().stream().map(c -> c.getName()).collect(Collectors.toList()));
    fields.addAll(table.getForeignKeys().stream().map(fk -> fk.getColumn().getName()).collect(Collectors.toList()));
    return fields;
  }

  public void setFilter(Tuple tuple) {

  }

  @Data
  public static class Pagination {
    private int limit = 10;
    private int offset = 0;
  }

  @Data
  public static class Sorting {
    private String field;
    private Direction direction = Direction.ASC;

    public static enum Direction {
      ASC(""),
      DESC("-");

      private final String value;

      Direction(String value) {
        this.value = value;
      }

      public String getValue() {
        return value;
      }
    }
  }
}
