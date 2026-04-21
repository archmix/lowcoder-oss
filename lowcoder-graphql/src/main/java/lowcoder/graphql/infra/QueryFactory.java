package lowcoder.graphql.infra;

import graphql.Scalars;
import graphql.schema.DataFetcher;
import graphql.schema.DataFetchingEnvironment;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLObjectType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.graphql.interfaces.Connection;
import lowcoder.graphql.interfaces.PageInfo;
import lowcoder.sql.application.SelectCommand;
import morphos.api.interfaces.Table;

import java.util.concurrent.CompletableFuture;

import static graphql.schema.GraphQLArgument.*;
import static lowcoder.graphql.infra.GraphQLNames.*;

public class QueryFactory {

  public static GraphQLFieldDefinition findByIdDefinition(Table table, GraphQLObjectType definition){
    return GraphQLFieldDefinition.newFieldDefinition()
      .name(fieldNameById(table))
      .argument(newArgument().name(idArgumentName()).type(Scalars.GraphQLID))
      .type(definition)
      .build();
  }

  public static String fieldNameById(Table table){
    return fieldName(table) + "ById";
  }

  @RequiredArgsConstructor(staticName = "of")
  @Slf4j
  public static class FindByIdFetcher implements DataFetcher<CompletableFuture<Object>> {

    private final Table table;
    private final SelectCommand selectCommand;

    @Override
    public CompletableFuture<Object> get(DataFetchingEnvironment environment) {
      var future = new CompletableFuture<Object>();

      Object id = environment.getArgument(idArgumentName());
      log.debug("Fetching {} by id {}", table.getName(), id);

      if (id == null) {
        future.complete(null);
        return future;
      }

      var searchOptions = SearchOptionsFactory.from(table, environment);
      selectCommand.findOne(searchOptions,future::complete,future::completeExceptionally);

      return future;
    }
  }

  @RequiredArgsConstructor(staticName = "of")
  @Slf4j
  public static class FindFetcher implements DataFetcher<CompletableFuture<Object>> {
    private final Table table;
    private final SelectCommand selectCommand;

    @Override
    @SuppressWarnings("unchecked")
    public CompletableFuture<Object> get(DataFetchingEnvironment environment) throws Exception {
      log.debug("Fetching {}", table.getName());

      var searchOptions = SearchOptionsFactory.from(table, environment);
      var future = new CompletableFuture<Object>();

      this.selectCommand.findPaged(searchOptions, paged -> {
        var pageInfo = PageInfo.of(
          paged.getLimit(),
          paged.getOffset(),
          paged.getOffset() + paged.getLimit() < paged.getTotal()
        );

        var connection = Connection.of(
          paged.getTotal(),
          paged.getData(),
          pageInfo
        );

        future.complete(connection);

      }, future::completeExceptionally);

      return future;
    }
  }
}
