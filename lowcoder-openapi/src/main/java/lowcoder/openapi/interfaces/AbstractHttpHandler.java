package lowcoder.openapi.interfaces;

import io.vertx.core.Handler;
import io.vertx.ext.web.RoutingContext;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import lowcoder.sql.infra.ConnectionPool;
import lowcoder.sql.infra.SelectSqlHandler;
import lowcoder.sql.interfaces.ExpandedFields;
import lowcoder.sql.interfaces.Fields;
import lowcoder.sql.interfaces.FilterOptions;
import lowcoder.sql.interfaces.FilterPredicate;
import lowcoder.sql.interfaces.PaginationOptions;
import lowcoder.sql.interfaces.SearchOptions;
import lowcoder.sql.interfaces.SortOptions;
import morphos.api.interfaces.Table;

import java.util.List;

import static java.util.Collections.*;
import static lowcoder.core.application.LowcoderContainerService.*;

@RequiredArgsConstructor
@Slf4j
public abstract class AbstractHttpHandler implements HttpHandler {
  protected final ConnectionPool pool;
  protected final Table table;

  protected Handler<Throwable> errorHandler(RoutingContext context) {
    return error -> {
      log.error("{} request for table {} failed", context.request().method(), table.getName(), error);
      HttpErrorResponse.handleError(context, error);
    };
  }

  @Override
  public final void handle(RoutingContext routingContext) {
      var requestId = routingContext.<String>get(X_REQUEST_ID);
      this.handle(routingContext, requestId);
  }

  public SearchOptions getSearchOptions(RoutingContext context) {
    var params = context.request().params();

    SortOptions sort = null;
    var pagination = PaginationOptions.create();
    var filter = FilterOptions.create();
    var fields = Fields.create(table, GetParameters.FIELDS.getAll(params));
    var expand = ExpandedFields.create(table, GetParameters.EXPAND.get(params));

    for(var pk : table.getPrimaryKeys()) {
      String name = pk.getName();
      String value = context.pathParam(name);
      if(value != null) {
        filter.and().add(pk.getFullName(), FilterPredicate.EQUALS, value);
      }
      sort = SortOptions.of(this.table, name);
    };

    for(String key : params.names()) {
      if(GetParameters.FIELDS.value().equals(key)) {
        continue;
      }
      if(GetParameters.EXPAND.value().equals(key)) {
        continue;
      }
      if(GetParameters.LIMIT.value().equals(key)) {
        pagination.setLimit(Long.parseLong(params.get(key)));
        continue;
      }
      if(GetParameters.OFFSET.value().equals(key)) {
        pagination.setOffset(Long.parseLong(params.get(key)));
        continue;
      }
      if(GetParameters.SORT.value().equals(key)) {
        sort = SortOptions.of(this.table, params.get(key));
        continue;
      }
      if(GetParameters.OR.value().equals(key)) {
        continue;
      }

      filter.and().addFilter(key, params.getAll(key));
    }

    var orParam = GetParameters.OR.get(params);
    if(orParam != null) {
      for(String param : List.of(orParam.split(","))){
        if(param.contains(":")) {
          var fieldValue = param.split(":");
          filter.or().addFilter(fieldValue[0], List.of(fieldValue[1]));
          continue;
        }
        filter.or().addFilter(param, emptyList());
      }
    }

    var selectSqlHandler = SelectSqlHandler.of(this.table, fields, expand, filter, sort, pagination);
    return SearchOptions.create(selectSqlHandler);
  }

  protected abstract void handle(RoutingContext context, String requestId);
}
