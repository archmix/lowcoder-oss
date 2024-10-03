package lowcoder.metadata.interfaces;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor(staticName = "create")
@Getter
public class PrimaryKey {
  private final String indexName;

  private final Column column;

  public Boolean getGenerated() {
    return this.column.getGenerated();
  }
}
