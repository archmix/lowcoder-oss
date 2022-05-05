package lowcoder.metadata.interfaces;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DatabaseSchema {
  private final String value;

  public static DatabaseSchema create(String value){
    if("null".equalsIgnoreCase(value)){
      return empty();
    }

    return new DatabaseSchema(value);
  }

  public static DatabaseSchema empty() {
    return new DatabaseSchema(null);
  }

  public String value() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
