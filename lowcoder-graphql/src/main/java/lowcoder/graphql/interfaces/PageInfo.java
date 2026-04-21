package lowcoder.graphql.interfaces;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLObjectType;
import lombok.Value;

@Value(staticConstructor = "of")
public class PageInfo {
  private static final String LIMIT_FIELD_NAME = "limit";
  private static final String OFFSET_FIELD_NAME = "offset";
  private static final String HAS_NEXT_PAGE_FIELD_NAME = "hasNextPage";

  Long limit;
  Long offset;
  Boolean hasNextPage;

  public static final GraphQLObjectType OBJECT_TYPE = GraphQLObjectType.newObject()
    .name("PageInfo")
    .description("Pagination metadata")
    .field(GraphQLFieldDefinition.newFieldDefinition().name(HAS_NEXT_PAGE_FIELD_NAME).type(Scalars.GraphQLBoolean))
    .field(GraphQLFieldDefinition.newFieldDefinition().name(OFFSET_FIELD_NAME).type(Scalars.GraphQLInt))
    .field(GraphQLFieldDefinition.newFieldDefinition().name(LIMIT_FIELD_NAME).type(Scalars.GraphQLInt))
    .build();

  public static GraphQLObjectType graphQLObjectType() {
    return OBJECT_TYPE;
  }

  public static GraphQLInputObjectType graphQLInputType() {
    return GraphQLInputObjectType.newInputObject()
      .name("PageInput")
      .field(GraphQLInputObjectField.newInputObjectField().name(LIMIT_FIELD_NAME).type(Scalars.GraphQLInt))
      .field(GraphQLInputObjectField.newInputObjectField().name(OFFSET_FIELD_NAME).type(Scalars.GraphQLInt))
      .build();
  }
}
