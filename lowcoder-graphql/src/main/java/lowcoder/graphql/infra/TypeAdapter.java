package lowcoder.graphql.infra;

import graphql.Scalars;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLInputType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLOutputType;
import morphos.api.interfaces.Field;

import static graphql.schema.GraphQLInputObjectField.*;
import static graphql.schema.GraphQLInputObjectType.*;

public enum TypeAdapter {
  BIT(Field.Type.BIT) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLBoolean;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.BOOLEAN.type();
    }
  },
  TINYINT(Field.Type.TINYINT) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLInt;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.INT.type();
    }
  },
  SMALLINT(Field.Type.SMALLINT) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLInt;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.INT.type();
    }
  },
  INTEGER(Field.Type.INTEGER) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLInt;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.INT.type();
    }
  },
  BIGINT(Field.Type.BIGINT) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLInt;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.INT.type();
    }
  },
  FLOAT(Field.Type.FLOAT) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLFloat;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.FLOAT.type();
    }
  },
  REAL(Field.Type.REAL) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLFloat;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.FLOAT.type();
    }
  },
  DOUBLE(Field.Type.DOUBLE) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLFloat;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.FLOAT.type();
    }
  },
  NUMERIC(Field.Type.NUMERIC) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLFloat;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.FLOAT.type();
    }
  },
  DECIMAL(Field.Type.DECIMAL) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLFloat;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.FLOAT.type();
    }
  },
  CHAR(Field.Type.CHAR),
  VARCHAR(Field.Type.VARCHAR),
  LONGVARCHAR(Field.Type.LONGVARCHAR),
  DATE(Field.Type.DATE){
    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.DATE.type();
    }
  },
  TIME(Field.Type.TIME){
    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.DATE.type();
    }
  },
  TIMESTAMP(Field.Type.TIMESTAMP){
    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.DATE.type();
    }
  },
  BINARY(Field.Type.BINARY),
  VARBINARY(Field.Type.VARBINARY),
  LONGVARBINARY(Field.Type.LONGVARBINARY),
  NULL(Field.Type.NULL),
  BLOB(Field.Type.BLOB),
  CLOB(Field.Type.CLOB),
  BOOLEAN(Field.Type.BOOLEAN) {
    @Override
    public GraphQLOutputType graphQLOutputType() {
      return Scalars.GraphQLBoolean;
    }

    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.BOOLEAN.type();
    }
  },
  NCHAR(Field.Type.NCHAR),
  NVARCHAR(Field.Type.NVARCHAR),
  LONG_NVARCHAR(Field.Type.LONG_NVARCHAR),
  NCLOB(Field.Type.NCLOB),
  TIME_WITH_TIMEZONE(Field.Type.TIME_WITH_TIMEZONE){
    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.DATE.type();
    }
  },
  TIMESTAMP_WITH_TIMEZONE(Field.Type.TIMESTAMP_WITH_TIMEZONE){
    @Override
    public GraphQLInputObjectType filterInputType() {
      return GraphQLFilters.DATE.type();
    }
  },
  DISTINCT(Field.Type.DISTINCT),
  OTHER(Field.Type.OTHER),
  UUID(Field.Type.UUID),
  ENUM(Field.Type.ENUM),
  XML(Field.Type.XML),
  MACADDR(Field.Type.MACADDR),
  INET(Field.Type.INET),
  CIDR(Field.Type.CIDR),
  JSON(Field.Type.JSON),
  ARRAY(Field.Type.ARRAY),
  INTERVAL(Field.Type.INTERVAL);

  private final Field.Type type;

  TypeAdapter(Field.Type type) {
    this.type = type;
  }

  public static TypeAdapter valueOf(Field field) {
    for (TypeAdapter adapter : TypeAdapter.values()) {
      if (field.getType().equals(adapter.type)) {
        return adapter;
      }
    }
    return TypeAdapter.OTHER;
  }

  public GraphQLInputType graphQLInputType() {
    return (GraphQLInputType) graphQLOutputType();
  }

  public GraphQLOutputType graphQLOutputType() {
    return Scalars.GraphQLString;
  }

  public GraphQLInputObjectType filterInputType() {
    return GraphQLFilters.STRING.type();
  }

  enum GraphQLFilters {

    INT(
      newInputObject()
        .name("IntFilter")
        .description("Filter for integer fields")
        .field(newInputObjectField().name("eq").type(Scalars.GraphQLInt))
        .field(newInputObjectField().name("ne").type(Scalars.GraphQLInt))
        .field(newInputObjectField().name("gt").type(Scalars.GraphQLInt))
        .field(newInputObjectField().name("gte").type(Scalars.GraphQLInt))
        .field(newInputObjectField().name("lt").type(Scalars.GraphQLInt))
        .field(newInputObjectField().name("lte").type(Scalars.GraphQLInt))
        .field(newInputObjectField().name("in").type(GraphQLList.list(Scalars.GraphQLInt)))
        .build()
    ),
    FLOAT(
      newInputObject()
        .name("FloatFilter")
        .description("Filter for float/decimal fields")
        .field(newInputObjectField().name("eq").type(Scalars.GraphQLFloat))
        .field(newInputObjectField().name("ne").type(Scalars.GraphQLFloat))
        .field(newInputObjectField().name("gt").type(Scalars.GraphQLFloat))
        .field(newInputObjectField().name("gte").type(Scalars.GraphQLFloat))
        .field(newInputObjectField().name("lt").type(Scalars.GraphQLFloat))
        .field(newInputObjectField().name("lte").type(Scalars.GraphQLFloat))
        .field(newInputObjectField().name("in")
          .type(GraphQLList.list(Scalars.GraphQLFloat)))
        .build()
    ),
    BOOLEAN(
      newInputObject()
        .name("BooleanFilter")
        .description("Filter for boolean fields")
        .field(newInputObjectField().name("eq").type(Scalars.GraphQLBoolean))
        .field(newInputObjectField().name("ne").type(Scalars.GraphQLBoolean))
        .build()
    ),
    STRING(
      newInputObject()
        .name("StringFilter")
        .description("Filter for string fields")
        .field(newInputObjectField().name("eq").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("ne").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("like").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("in").type(GraphQLList.list(Scalars.GraphQLString)))
        .build()
    ),

    DATE(
      newInputObject()
        .name("DateFilter")
        .description("Filter for date/time fields (ISO-8601)")
        .field(newInputObjectField().name("eq").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("ne").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("gt").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("gte").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("lt").type(Scalars.GraphQLString))
        .field(newInputObjectField().name("lte").type(Scalars.GraphQLString))
        .build()
    );

    private final GraphQLInputObjectType inputType;

    GraphQLFilters(GraphQLInputObjectType inputType) {
      this.inputType = inputType;
    }

    public GraphQLInputObjectType type() {
      return inputType;
    }
  }
}