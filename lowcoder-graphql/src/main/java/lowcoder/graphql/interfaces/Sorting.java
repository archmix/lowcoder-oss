package lowcoder.graphql.interfaces;

import graphql.Scalars;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLEnumType;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import lowcoder.sql.interfaces.SortOptions;
import morphos.api.interfaces.Table;

import java.util.List;
import java.util.Map;

public class Sorting {

  private static final GraphQLEnumType ENUM_TYPE = GraphQLEnumType.newEnum().name("Sort")
    .description("Sorting directions: ASC or DESC")
    .value(SortOptions.Direction.ASC.name(), SortOptions.Direction.ASC)
    .value(SortOptions.Direction.DESC.name(), SortOptions.Direction.DESC).build();

  private static final GraphQLInputObjectType INPUT_TYPE = GraphQLInputObjectType.newInputObject()
    .name("SortInput")
    .description("Sorting options")
    .field(GraphQLInputObjectField.newInputObjectField()
      .name("field")
      .type(Scalars.GraphQLString))
    .field(GraphQLInputObjectField.newInputObjectField()
      .name("direction")
      .type(ENUM_TYPE))
    .build();

  public static GraphQLEnumType graphQLEnumType() {
    return ENUM_TYPE;
  }

  public static GraphQLInputObjectType graphQLInputType() {
    return INPUT_TYPE;
  }

  public static SortOptions toSortOptions(Table table, DataFetchingEnvironment env) {
    var sortInput = env.getArgument("sort");

    if (sortInput == null) {
      return null;
    }

    var sortList = (List<Map<String, Object>>) sortInput;

    if (sortList.isEmpty()) {
      return null;
    }

    var sort = sortList.get(0);
    var field = (String) sort.get("field");
    var direction = (SortOptions.Direction) sort.get("direction");

    var sortExpression = direction.toExpression(field);
    return SortOptions.of(table, sortExpression);
  }
}
