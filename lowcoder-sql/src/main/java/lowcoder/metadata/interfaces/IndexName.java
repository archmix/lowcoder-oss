package lowcoder.metadata.interfaces;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class IndexName {
  private final String value;

  public static IndexName create(String name) {
    return new IndexName(name.toLowerCase());
  }

  public String value() {
    return value;
  }

  public String toString() {
    return this.value;
  }
}
