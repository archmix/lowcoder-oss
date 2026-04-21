package lowcoder.sql.interfaces;

import io.vertx.sqlclient.Tuple;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lowcoder.sql.infra.TypeAdapter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
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

  public void setValues(FieldAliases aliases, Tuple tuple) {
    conditionalCriteria.get(Condition.AND).forEach(criteria -> criteria.setValue(aliases, tuple));
    conditionalCriteria.get(Condition.OR).forEach(criteria -> criteria.setValue(aliases, tuple));
  }

  public Criteria and() {
    return Criteria.create(conditionalCriteria.get(Condition.AND));
  }

  public Criteria or() {
    return Criteria.create(conditionalCriteria.get(Condition.OR));
  }

  public String getSql(FieldAliases aliases) {
    StringBuilder sql = new StringBuilder(" WHERE 1=1");
    Function<Criterion, String> getStatement = criterion -> criterion.getStatement(aliases);

    var andCriteria = conditionalCriteria.get(Condition.AND);
    var andSql = andCriteria.stream().map(getStatement).collect(Collectors.joining(" AND "));
    if (!andSql.isEmpty()) {
      sql.append(" AND (").append(andSql).append(")");
    }

    var orCriteria = conditionalCriteria.get(Condition.OR);
    var orSql = orCriteria.stream().map(getStatement).collect(Collectors.joining(" OR "));
    if (!orSql.isEmpty()) {
      sql.append(" OR (").append(orSql).append(")");
    }

    return sql.toString();
  }

  enum Condition {
    AND,
    OR;
  }

  static class Criterion {
    private final String fieldName;
    private final FilterPredicate predicate;
    private final Object value;

    private Criterion(String fieldName, FilterPredicate predicate, Object value) {
      this.fieldName = fieldName;
      this.predicate = predicate;
      this.value = value;
    }

    public static Criterion create(String fieldName, FilterPredicate predicate, Object value) {
      return new Criterion(fieldName, predicate, value);
    }

    public String getStatement(FieldAliases aliases) {
      var alias = aliases.getAliasByFieldName(this.fieldName).getFullName();

      if (predicate == FilterPredicate.IN) {
        var clause = ((Collection<?>) value).stream().map(value -> "?").collect(Collectors.joining(","));
        return alias + " " + predicate.keyword() + " (" + clause + ")";
      }

      if (predicate == FilterPredicate.IS_NULL || predicate == FilterPredicate.IS_NOT_NULL) {
        return alias + " " + predicate.keyword();
      }

      return alias + " " + predicate.keyword() + " ? ";
    }

    public void setValue(FieldAliases aliases, Tuple tuple) {
      var type = TypeAdapter.valueOf(aliases.getAliasByFieldName(this.fieldName).getField());

      if (predicate == FilterPredicate.IN) {
        ((Collection<String>) value).forEach(value -> type.setValue(tuple, value));
        return;
      }

      if (predicate == FilterPredicate.IS_NULL || predicate == FilterPredicate.IS_NOT_NULL) {
        return;
      }

      type.setValue(tuple, value);
    }
  }
}