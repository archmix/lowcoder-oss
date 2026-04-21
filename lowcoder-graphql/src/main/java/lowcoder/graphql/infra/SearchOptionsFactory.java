package lowcoder.graphql.infra;

import graphql.schema.DataFetchingEnvironment;
import graphql.schema.DataFetchingFieldSelectionSet;
import graphql.schema.SelectedField;
import lowcoder.sql.infra.SelectSqlHandler;
import lowcoder.sql.interfaces.Criteria;
import lowcoder.sql.interfaces.ExpandedFields;
import lowcoder.sql.interfaces.Fields;
import lowcoder.sql.interfaces.FilterOptions;
import lowcoder.sql.interfaces.FilterPredicate;
import lowcoder.sql.interfaces.PaginationOptions;
import lowcoder.sql.interfaces.SearchOptions;
import morphos.api.interfaces.Table;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static lowcoder.graphql.interfaces.Sorting.*;

public class SearchOptionsFactory {
  private static final String LIMIT = "limit";
  private static final String OFFSET = "offset";

  public static SearchOptions from(Table table, DataFetchingEnvironment env) {
    var fields = toFields(table, env.getSelectionSet());
    var expanded = toExpandedFields(table, env.getSelectionSet());
    var filter = toFilterOptions(table, env);
    var sort = toSortOptions(table, env);
    var pagination = toPaginationOptions(env);

    var handler = SelectSqlHandler.of(table, fields, expanded,filter,sort,pagination);

    return SearchOptions.create(handler);
  }

  private static PaginationOptions toPaginationOptions( DataFetchingEnvironment env) {
    var pagination = PaginationOptions.create();

    Optional.ofNullable(env.getArgument(LIMIT))
      .filter(Long.class::isInstance)
      .map(Long.class::cast)
      .ifPresent(pagination::setLimit);

    Optional.ofNullable(env.getArgument(OFFSET))
      .filter(Long.class::isInstance)
      .map(Long.class::cast)
      .ifPresent(pagination::setOffset);

    return pagination;
  }

  private static Fields toFields(Table table, DataFetchingFieldSelectionSet selectionSet) {
    Collection<String> fieldNames =
      selectionSet.getFields().stream()
        .filter(SearchOptionsFactory::isScalar)
        .map(SelectedField::getName)
        .filter(name -> table.getField(name).isPresent())
        .collect(Collectors.toSet());

    return Fields.create(table, fieldNames);
  }

  private static ExpandedFields toExpandedFields(Table table,DataFetchingFieldSelectionSet selectionSet) {

    String expand =
      selectionSet.getFields().stream()
        .filter(field -> !isScalar(field))
        .map(SelectedField::getName)
        .filter(name -> !name.startsWith("__"))
        .filter(name -> table.getForeignKey(name).isPresent())
        .distinct()
        .collect(Collectors.joining(","));

    return ExpandedFields.create(table, expand);
  }

  private static boolean isScalar(SelectedField field) {
    return field.getSelectionSet() == null || field.getSelectionSet().getFields().isEmpty();
  }

  private static FilterOptions toFilterOptions(Table table, DataFetchingEnvironment env) {

    var filterInput = (Map<String, Object>) env.getArgument("filter");

    if (filterInput == null || filterInput.isEmpty()) {
      return FilterOptions.create();
    }

    FilterOptions filterOptions = FilterOptions.create();
    applyFilter(table, filterInput, filterOptions, filterOptions.and());
    return filterOptions;
  }

  private static void applyFilter(Table table, Map<String, Object> input, FilterOptions filterOptions, Criteria criteria) {
    input.forEach((key, value) -> {
      var nestedCriteria = nestedCriteria(filterOptions, key);
      if(nestedCriteria != null) {
        ((List<Map<String, Object>>) value).forEach(nested ->
          applyFilter(table, nested, filterOptions, nestedCriteria)
        );
        return;
      }

      var field = table.getField(key).orElseThrow(() -> new IllegalArgumentException("Unknown filter field: " + key));

      var predicates = (Map<String, Object>) value;
      predicates.forEach((predicateKey, predicateValue) -> {
        var predicate = FilterPredicate.valueOf(predicateKey.toUpperCase());
        criteria.add(field.getName(), predicate, predicateValue);
      });
    });
  }

  private static Criteria nestedCriteria(FilterOptions filterOptions, String key) {
    if(key.equals("AND")) {
      return filterOptions.and();
    }

    if(key.equals("OR")) {
      return filterOptions.or();
    }

    return null;
  }
}

