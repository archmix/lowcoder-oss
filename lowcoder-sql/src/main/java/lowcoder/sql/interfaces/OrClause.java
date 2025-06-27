package lowcoder.sql.interfaces;

import lowcoder.metadata.interfaces.Field;

import java.util.function.Function;

public class OrClause implements Function<Field, String> {
  private static final OrClause OR_CLAUSE = new OrClause();

  public static OrClause of() {
    return OR_CLAUSE;
  }

  private final SetExpression setExpression = SetExpression.of();

  @Override
  public String apply(Field field) {
    return " OR " + setExpression.apply(field);
  }
}
