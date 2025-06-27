package lowcoder.sql.interfaces;

import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lowcoder.metadata.interfaces.Column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
public class FilterOptions {
  private final Map<Condition, List<Criterion>> conditionalCriteria;

  public static FilterOptions create() {
    var conditionalCriteria = new HashMap<Condition, List<Criterion>>();
    conditionalCriteria.put(Condition.AND, new ArrayList<>());
    conditionalCriteria.put(Condition.OR, new ArrayList<>());
    return new FilterOptions(conditionalCriteria);
  }

  public void setValues(Tuple tuple){
    conditionalCriteria.get(Condition.AND).forEach(criteria -> criteria.setValue(tuple));
    conditionalCriteria.get(Condition.OR).forEach(criteria -> criteria.setValue(tuple));
  }

  public Criteria and(){
    return Criteria.create(conditionalCriteria.get(Condition.AND));
  }

  public Criteria or() {
    return Criteria.create(conditionalCriteria.get(Condition.OR));
  }

  public String getSQL(){
    StringBuilder sql = new StringBuilder(" WHERE 1=1");

    var andCriteria = conditionalCriteria.get(Condition.AND);
    var andSql = andCriteria.stream().map(Criterion::getStatement).collect(Collectors.joining(" AND "));
    if(!andSql.isEmpty()) {
      sql.append(" AND (").append(andSql).append(")");
    }

    var orCriteria = conditionalCriteria.get(Condition.OR);
    var orSql = orCriteria.stream().map(Criterion::getStatement).collect(Collectors.joining(" OR "));
    if(!orSql.isEmpty()) {
      sql.append(" OR (").append(orSql).append(")");
    }

    return sql.toString();
  }

  enum Condition {
    AND,
    OR;
  }

  @RequiredArgsConstructor(staticName = "create")
  static class Criterion {
    private final Column column;
    private final Predicate predicate;
    private final Object value;

    public String getStatement() {
      if(predicate == Predicate.IN) {
        var clause = ((Collection<?>) value).stream().map(value -> "?").collect(Collectors.joining(","));
        return column.getName() + " " + predicate.keyword() + " (" + clause +")";
      }

      if(predicate == Predicate.IS_NULL || predicate == Predicate.IS_NOT_NULL) {
        return column.getName() + " " + predicate.keyword();
      }

      return column.getName() + " " + predicate.keyword() + " ? ";
    }

    public void setValue(Tuple tuple){
      if(predicate == Predicate.IN) {
        ((Collection<String>) value).forEach(value -> column.getType().setValue(tuple, value));
        return;
      }

      if(predicate == Predicate.IS_NULL || predicate == Predicate.IS_NOT_NULL) {
        return;
      }

      column.getType().setValue(tuple, value);
    }
  }


}
