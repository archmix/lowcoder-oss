package lowcoder.graphql.infra;

import morphos.api.interfaces.Field;
import morphos.api.interfaces.Table;

import java.util.Arrays;
import java.util.stream.Collectors;

public class GraphQLNames {

  public static String typeName(Table table){
    return typeName(table.getName());
  }

  public static String fieldName(Table table) {
    return fieldName(table.getName());
  }

  public static String fieldName(Field field) {
    return fieldName(field.getName());
  }

  public static String filterTypeName(Table table) {
    return typeFilterName(table.getName());
  }

  public static String queryTypeName(){
    return "Query";
  }

  public static String mutationTypeName(){
    return "Mutation";
  }

  public static String idArgumentName(){
    return "id";
  }

  public static String inputArgumentName() {
    return "input";
  }

  public static String filterArgumentName() {
    return "filter";
  }

  public static String sortArgumentName() {
    return "sort";
  }

  public static String pageArgumentName() {
    return "page";
  }

  private static String typeFilterName(String input) {
    return typeName(input) + "Filter";
  }

  private static String typeName(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    return Arrays.stream(input.split("_"))
      .filter(s -> !s.isEmpty())
      .map(s -> s.substring(0, 1).toUpperCase() + s.substring(1))
      .collect(Collectors.joining());
  }

  private static String fieldName(String input) {
    if (input == null || input.isEmpty()) {
      return input;
    }

    return input.toLowerCase();
  }

}