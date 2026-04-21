package lowcoder.graphql.infra;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLTypeReference;
import io.vertx.core.json.JsonObject;
import lombok.RequiredArgsConstructor;
import lowcoder.sql.application.UpdateCommand;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static graphql.schema.GraphQLArgument.*;
import static graphql.schema.GraphQLFieldDefinition.*;
import static lowcoder.graphql.infra.GraphQLNames.*;

public class UpdateMutationFactory {

  public static GraphQLInputObjectType graphQLInputType(Table table) {
    var builder = GraphQLInputObjectType.newInputObject()
      .name(updateInputName(table));

    table.getFields().forEach(field -> {
      if (field instanceof PrimaryKey) {
        return;
      }

      if (field instanceof ForeignKey) {
        var fk = (ForeignKey) field;

        builder.field(
          GraphQLInputObjectField.newInputObjectField()
            .name(GraphQLNames.fieldName(fk))
            .type(Scalars.GraphQLID)
            .build()
        );
        return;
      }

      var adapter = TypeAdapter.valueOf(field);

      builder.field(
        GraphQLInputObjectField.newInputObjectField()
          .name(GraphQLNames.fieldName(field))
          .type(adapter.graphQLInputType())
          .build()
      );
    });

    return builder.build();
  }

  public static GraphQLFieldDefinition graphQLFieldDefinition(Table table) {
    String typeName = typeName(table);

    return newFieldDefinition()
      .name(fieldName(table))
      .argument(newArgument().name(idArgumentName()).type(Scalars.GraphQLID))
      .argument(newArgument().name(inputArgumentName()).type(GraphQLTypeReference.typeRef(updateInputName(table))))
      .type(GraphQLTypeReference.typeRef(typeName))
      .build();
  }

  public static String fieldName(Table table) {
    return "update" + typeName(table);
  }

  private static String updateInputName(Table table) {
    return typeName(table) + "UpdateInput";
  }

  @RequiredArgsConstructor(staticName = "of")
  public static class GraphQLUpdateFetcher implements DataFetcher<Object> {

    private final Table table;
    private final UpdateCommand updateCommand;

    @Override
    public Object get(DataFetchingEnvironment env) {
      var future = new CompletableFuture<Object>();

      try {
        var id = env.getArgument(idArgumentName());


        Map<String, Object> input = env.getArgument(inputArgumentName());

        var json = JsonObject.mapFrom(input);

        table.getFields().stream()
          .filter(f -> f instanceof PrimaryKey)
          .findFirst()
          .ifPresent(pk -> json.put(pk.getName(), id));

        updateCommand.execute(
          json,
          v -> future.complete(json.getMap()),
          future::completeExceptionally
        );
      } catch (Throwable e) {
        future.completeExceptionally(e);
      }

      return future;
    }
  }
}
