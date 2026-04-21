package lowcoder.graphql.application;

import graphql.schema.DataFetcher;
import graphql.schema.DataFetcherFactory;
import graphql.schema.DataFetcherFactoryEnvironment;
import graphql.schema.GraphQLCodeRegistry;
import graphql.schema.idl.RuntimeWiring;
import io.vertx.core.json.JsonObject;
import io.vertx.ext.web.handler.graphql.schema.VertxPropertyDataFetcher;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.graphql.infra.CreateMutationFactory;
import lowcoder.graphql.infra.QueryFactory;
import lowcoder.graphql.infra.UpdateMutationFactory;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.infra.InsertSqlHandler;
import lowcoder.sql.infra.UpdateSqlHandler;
import morphos.api.interfaces.Table;

import java.util.Collection;

import static graphql.schema.FieldCoordinates.*;
import static lowcoder.graphql.infra.GraphQLNames.*;
import static lowcoder.graphql.infra.QueryFactory.*;

@RequiredArgsConstructor(staticName = "of")
@Slf4j
public class RuntimeWiringBuilder {
  private final Collection<Table> tables;
  private final ConnectionPool connectionPool;

  public RuntimeWiring build() {
    var codeRegistry = GraphQLCodeRegistry.newCodeRegistry();
    codeRegistry.defaultDataFetcher(new JsonObjectDataFetcherFactory());

    var selectCommand = connectionPool.selectCommand();

    tables.forEach(table -> {
      String fieldNameById = fieldNameById(table);
      log.debug("Registering {} on runtime wiring query field {}", QueryFactory.FindByIdFetcher.class.getName(), fieldNameById);
      codeRegistry.dataFetcher(coordinates(queryTypeName(), fieldNameById), QueryFactory.FindByIdFetcher.of(table, selectCommand));

      var fieldName = fieldName(table);
      log.debug("Registering {} on Query.{}", QueryFactory.FindFetcher.class.getName(), fieldName);
      codeRegistry.dataFetcher(coordinates(queryTypeName(), fieldName), QueryFactory.FindFetcher.of(table, selectCommand));
    });

    tables.forEach(table -> {
      var insertCommand = connectionPool.insertCommand(InsertSqlHandler.of(table));
      var updateCommand = connectionPool.updateCommand(UpdateSqlHandler.of(table));

      var createField = CreateMutationFactory.fieldName(table);
      log.debug("Registering {} on Mutation.{}", CreateMutationFactory.GraphQLCreateFetcher.class.getName(), createField);
      codeRegistry.dataFetcher(
        coordinates(mutationTypeName(), createField),
        CreateMutationFactory.GraphQLCreateFetcher.of(table, insertCommand)
      );

      var updateField = UpdateMutationFactory.fieldName(table);
      log.debug("Registering {} on Mutation.{}", UpdateMutationFactory.GraphQLUpdateFetcher.class.getName(), updateField);
      codeRegistry.dataFetcher(
        coordinates(mutationTypeName(), updateField),
        UpdateMutationFactory.GraphQLUpdateFetcher.of(table, updateCommand)
      );
    });

    return RuntimeWiring.newRuntimeWiring().codeRegistry(codeRegistry).build();
  }

  static class JsonObjectDataFetcherFactory implements DataFetcherFactory<Object> {
    @Override
    public DataFetcher<Object> get(DataFetcherFactoryEnvironment environment) {
      String fieldName = environment.getFieldDefinition().getName();

      return env -> {
        Object source = env.getSource();

        if (source instanceof JsonObject) {
          var json = (JsonObject) source;
          return json.getValue(fieldName);
        }

        return VertxPropertyDataFetcher.create(fieldName).get(env);
      };
    }
  }
}
