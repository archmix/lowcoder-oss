package lowcoder.sql.interfaces;

import lombok.RequiredArgsConstructor;

import java.util.Collection;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RequiredArgsConstructor(staticName = "create")
public class Criteria {
  private final Collection<FilterOptions.Criterion> criteria;

  public Criteria add(String fieldName, FilterPredicate predicate, Object value){
    this.criteria.add(FilterOptions.Criterion.create(fieldName, predicate, value));
    return this;
  }

  public void addFilter(String filter, Collection<String> values) {
    var namedFilter = NamedFilter.create(filter);

    if(namedFilter.getPredicate() == FilterPredicate.IN) {
      var inValues = values.stream().filter(value -> value.contains(",")).map(value -> value.split(","))
        .flatMap(Stream::of).collect(Collectors.toList());

      this.addFilter(namedFilter, inValues);
      return;
    }

    var value = values.stream().findFirst().orElse(null);
    this.addFilter(namedFilter, value);
  }

  private void addFilter(NamedFilter namedFilter, Object value) {
    this.add(namedFilter.getName(), namedFilter.getPredicate(), value);
  }
}
