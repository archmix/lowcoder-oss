package lowcoder.metadata.interfaces;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor()
public class DatabaseCatalog {
  private final String value;

  public static DatabaseCatalog create(String value) {
    if(value == null || "null".equalsIgnoreCase(value)){
      return empty();
    }
    return new DatabaseCatalog(value);
  }

  public static DatabaseCatalog empty(){
    return new DatabaseCatalog(null);
  }

  public String value() {
    return this.value;
  }

  @Override
  public String toString() {
    return this.value;
  }
}
