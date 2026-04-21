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
import lowcoder.promise.interfaces.FuturePromise;
import lowcoder.sql.application.InsertCommand;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static graphql.schema.GraphQLArgument.*;
import static graphql.schema.GraphQLFieldDefinition.*;
import static lowcoder.graphql.infra.GraphQLNames.*;

public class CreateMutationFactory {

  public static GraphQLFieldDefinition graphQLFieldDefinition(Table table) {
    var typeName = typeName(table);
    var inputName = inputName(table);

    return newFieldDefinition()
      .name(fieldName(table))
      .argument(newArgument().name(inputArgumentName()).type(GraphQLTypeReference.typeRef(inputName)))
      .type(GraphQLTypeReference.typeRef(typeName))
      .build();
  }

  public static String fieldName(Table table) {
    return "create" + typeName(table);
  }

  public static GraphQLInputObjectType graphQLInputType(Table table) {
    var builder = GraphQLInputObjectType.newInputObject()
      .name(inputName(table));

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

  private static String inputName(Table table) {
    return typeName(table) + "CreateInput";
  }

  @RequiredArgsConstructor(staticName = "of")
  public static class GraphQLCreateFetcher implements DataFetcher<CompletableFuture<Object>> {
    private final Table table;
    private final InsertCommand insertCommand;

    @Override
    public CompletableFuture<Object> get(DataFetchingEnvironment env) {
      var future = new CompletableFuture<Object>();

      Map<String, Object> input = env.getArgument(inputArgumentName());
      var json = JsonObject.mapFrom(input);

      insertCommand.executeAndGetGeneratedKeys(json, generatedIds -> {
        generatedIds.fieldNames().forEach(key -> json.put(key, generatedIds.getValue(key)));
        future.complete(json.getMap());
      }, future::completeExceptionally);

      return future;
    }
  }
}
