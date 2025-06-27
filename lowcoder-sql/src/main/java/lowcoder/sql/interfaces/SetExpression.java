package lowcoder.sql.interfaces;

import lowcoder.metadata.interfaces.Field;

import java.util.function.Function;

public class SetExpression implements Function<Field, String> {
  private static final SetExpression SET_EXPRESSION = new SetExpression();

  public static SetExpression of() {
    return SET_EXPRESSION;
  }

  @Override
  public String apply(Field field) {
    return field.getName() + " = ?";
  }
}
