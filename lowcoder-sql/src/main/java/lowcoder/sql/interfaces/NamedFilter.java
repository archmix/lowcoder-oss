package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class NamedFilter {
  private final String name;
  private final Predicate predicate;

  public static NamedFilter create(String filter) {
    var field = filter;
    var predicate = "";

    if(filter.contains("[")) {
      field = filter.substring(0, filter.indexOf('['));
      predicate = filter.substring(filter.indexOf('[') + 1, filter.indexOf(']'));
    }

    return new NamedFilter(field, Predicate.of(predicate));
  }
}