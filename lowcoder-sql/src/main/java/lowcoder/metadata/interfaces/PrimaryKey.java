package lowcoder.metadata.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
@Getter
public class PrimaryKey implements Field {
  private final String indexName;

  private final Column column;

  public Boolean getGenerated() {
    return this.column.getGenerated();
  }

  public String getName() {
    return this.column.getName();
  }
}
