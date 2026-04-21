package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import morphos.api.interfaces.Field;
import morphos.api.interfaces.Table;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SortOptions {
  @Getter
  private final Field field;
  private final Direction direction;

  public static SortOptions of(Table table, String sorting) {
    var direction = Direction.from(sorting);
    var fieldName = sorting;

    if (direction == Direction.DESC) {
      fieldName = sorting.replaceFirst(Direction.DESC.value(), "");
    }

    var field = table.getField(fieldName).orElseThrow(() -> new IllegalArgumentException("Sorting field not found in table"));
    return new SortOptions(field, direction);
  }

  public String getSql(FieldAliases aliases) {
    return " ORDER BY " + aliases.getAlias(field).getFullName() + " " + this.direction.keyword();
  }

  public enum Direction {
    ASC("") {
      @Override
      String keyword() {
        return "";
      }
    },
    DESC("-") {
      @Override
      String keyword() {
        return "DESC";
      }
    };

    private final String value;

    Direction(String value) {
      this.value = value;
    }

    public static Direction from(String field) {
      if (field.contains(DESC.value())) {
        return DESC;
      }
      return ASC;
    }

    public String value() {
      return value;
    }

    public String toExpression(String field) {
      return this.value + field;
    }

    abstract String keyword();
  }
}