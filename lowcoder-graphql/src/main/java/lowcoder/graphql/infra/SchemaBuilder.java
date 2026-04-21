package lowcoder.graphql.infra;

import graphql.Scalars;
import graphql.schema.GraphQLFieldDefinition;
import graphql.schema.GraphQLInputObjectField;
import graphql.schema.GraphQLInputObjectType;
import graphql.schema.GraphQLList;
import graphql.schema.GraphQLObjectType;
import graphql.schema.GraphQLSchema;
import graphql.schema.GraphQLType;
import graphql.schema.GraphQLTypeReference;
import graphql.schema.idl.RuntimeWiring;
import lowcoder.graphql.interfaces.Connection;
import lowcoder.graphql.interfaces.PageInfo;
import lowcoder.graphql.interfaces.Sorting;
import morphos.api.interfaces.Field;
import morphos.api.interfaces.ForeignKey;
import morphos.api.interfaces.Table;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static graphql.schema.GraphQLArgument.*;
import static graphql.schema.GraphQLFieldDefinition.*;
import static graphql.schema.GraphQLObjectType.*;
import static lowcoder.graphql.infra.GraphQLNames.*;
import static lowcoder.graphql.interfaces.Sorting.*;

public class SchemaBuilder {
  private GraphQLObjectType query;
  private GraphQLObjectType mutation;
  private final Set<GraphQLType> additionalTypes = new HashSet<>();

  public static SchemaBuilder create(Collection<Table> tables) {
    return new SchemaBuilder().tables(tables);
  }

  private SchemaBuilder tables(Collection<Table> tables) {
    var queryFields = new ArrayList<GraphQLFieldDefinition>();
    var mutationFields = new ArrayList<GraphQLFieldDefinition>();
    var types = new HashSet<GraphQLType>();

    types.add(Sorting.graphQLEnumType());

    var sortInput = graphQLInputType();
    types.add(sortInput);

    types.add(PageInfo.graphQLObjectType());

    var pageInput = PageInfo.graphQLInputType();
    types.add(pageInput);

    tables.forEach(table -> {
      var fields = buildFields(table.getFields());
      var relationFields = buildRelationFields(table);

      var definition = newObject().name(typeName(table)).fields(fields).fields(relationFields).build();
      types.add(definition);

      var filterInput = buildTableFilter(table);
      types.add(filterInput);

      var connectionType = Connection.graphQLObjectType(table, definition);
      types.add(connectionType);

      var queryField = newFieldDefinition()
        .name(fieldName(table))
        .argument(newArgument().name(filterArgumentName()).type(filterInput))
        .argument(newArgument().name(sortArgumentName()).type(GraphQLList.list(sortInput)))
        .argument(newArgument().name(pageArgumentName()).type(pageInput))
        .type(connectionType)
        .build();
      queryFields.add(queryField);

      queryFields.add(QueryFactory.findByIdDefinition(table, definition));

      types.add(CreateMutationFactory.graphQLInputType(table));
      types.add(UpdateMutationFactory.graphQLInputType(table));

      mutationFields.add(CreateMutationFactory.graphQLFieldDefinition(table));
      mutationFields.add(UpdateMutationFactory.graphQLFieldDefinition(table));
      //mutationFields.add(buildDeleteMutation(table));
    });

    this.query = newObject().name(queryTypeName()).fields(queryFields).build();

    this.mutation = newObject().name(mutationTypeName()).fields(mutationFields).build();

    this.additionalTypes.addAll(types);

    return this;
  }

  private GraphQLFieldDefinition buildDeleteMutation(Table table) {
    String typeName = typeName(table);

    return newFieldDefinition()
      .name("delete" + typeName)
      .argument(newArgument()
        .name("id")
        .type(Scalars.GraphQLID))
      .type(Scalars.GraphQLBoolean)
      .build();
  }

  private GraphQLInputObjectType buildTableFilter(Table table) {
    String filterName = filterTypeName(table);

    var builder = GraphQLInputObjectType.newInputObject().name(filterName);

    builder
      .field(GraphQLInputObjectField.newInputObjectField()
        .name("AND")
        .type(GraphQLList.list(GraphQLTypeReference.typeRef(filterName))))
      .field(GraphQLInputObjectField.newInputObjectField()
        .name("OR")
        .type(GraphQLList.list(GraphQLTypeReference.typeRef(filterName))))
      .field(GraphQLInputObjectField.newInputObjectField()
        .name("NOT")
        .type(GraphQLTypeReference.typeRef(filterName)));

    table.getFields().forEach(field -> {
      if (field instanceof ForeignKey) {
        var fk = (ForeignKey) field;
        var refTable = fk.getReferencedTable();

        builder.field(
          GraphQLInputObjectField.newInputObjectField()
            .name(fieldName(refTable))
            .type(GraphQLTypeReference.typeRef(filterTypeName(refTable)))
            .build()
        );
        return;
      }

      var adapter = TypeAdapter.valueOf(field);

      builder.field(
        GraphQLInputObjectField.newInputObjectField()
          .name(fieldName(field))
          .type(adapter.filterInputType())
          .build()
      );
    });

    return builder.build();
  }

  private List<GraphQLFieldDefinition> buildRelationFields( Table table) {
    var relations = new ArrayList<GraphQLFieldDefinition>();

    table.getForeignKeys().forEach(fk -> {
      var referencedTable = fk.getReferencedTable();
      var refTypeName = typeName(referencedTable);

      relations.add(
        GraphQLFieldDefinition.newFieldDefinition().name(fieldName(referencedTable)).type(GraphQLTypeReference.typeRef(refTypeName)).build()
      );
    });

    return relations;
  }

  private List<GraphQLFieldDefinition> buildFields(Collection<Field> fields) {
    var definitions = new ArrayList<GraphQLFieldDefinition>();

    fields.forEach(field ->{
      if(field instanceof ForeignKey) {
        return;
      }
      var definition = newFieldDefinition().name(fieldName(field))
        .type(TypeAdapter.valueOf(field).graphQLOutputType()).build();
      definitions.add(definition);
    });

    return definitions;
  }

  public GraphQLSchema build(RuntimeWiring runtimeWiring) {
    return GraphQLSchema.newSchema().query(this.query).mutation(this.mutation).additionalTypes(this.additionalTypes)
      .codeRegistry(runtimeWiring.getCodeRegistry()).build();
  }
}
