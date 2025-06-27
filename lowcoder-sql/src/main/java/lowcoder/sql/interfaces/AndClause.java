package lowcoder.sql.interfaces;

import lowcoder.metadata.interfaces.Field;

import java.util.function.Function;

public class AndClause implements Function<Field, String> {
  private static final AndClause AND_CLAUSE = new AndClause();

  public static AndClause of() {
    return AND_CLAUSE;
  }

  private final SetExpression setExpression = SetExpression.of();

  @Override
  public String apply(Field field) {
    return " AND " + setExpression.apply(field);
  }
}
