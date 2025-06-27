package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class SortOptions {
  private final String field;
  private final Direction direction;

  public static SortOptions of(String field) {
    var direction = Direction.from(field);
    if (direction == Direction.DESC) {
      return new SortOptions(field.replaceFirst(Direction.DESC.value(), ""), direction);
    }

    return new SortOptions(field, direction);
  }

  public String getSQL() {
    return " ORDER BY " + this.field + " " + this.direction.keyword();
  }

  public static enum Direction {
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

    abstract String keyword();
  }
}