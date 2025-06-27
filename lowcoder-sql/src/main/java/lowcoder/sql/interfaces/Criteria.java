package lowcoder.sql.interfaces;

import lombok.RequiredArgsConstructor;
import lowcoder.metadata.interfaces.Column;

import java.util.Collection;

@RequiredArgsConstructor(staticName = "create")
public class Criteria {
  private final Collection<FilterOptions.Criterion> criteria;

  public Criteria add(Column column, Predicate predicate, Object value){
    this.criteria.add(FilterOptions.Criterion.create(column, predicate, value));
    return this;
  }
}
