package lowcoder.graphql.interfaces;

import graphql.Scalars;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import io.vertx.core.json.JsonArray;
import lombok.Value;
import morphos.api.interfaces.Table;

import static graphql.schema.GraphQLFieldDefinition.*;
import static lowcoder.graphql.infra.GraphQLNames.*;

@Value(staticConstructor = "of")
public class Connection {
  Long total;
  JsonArray nodes;
  PageInfo pageInfo;

  public static GraphQLObjectType graphQLObjectType(Table table,GraphQLObjectType nodeType) {
    return GraphQLObjectType.newObject()
      .name(typeName(table) + "Connection")
      .field(newFieldDefinition().name("total").type(Scalars.GraphQLInt))
      .field(newFieldDefinition().name("nodes").type(GraphQLList.list(nodeType)))
      .field(newFieldDefinition().name("pageInfo").type(PageInfo.graphQLObjectType()))
      .build();
  }
}
