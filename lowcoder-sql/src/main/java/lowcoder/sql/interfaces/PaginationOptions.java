package lowcoder.sql.interfaces;

import lombok.Getter;
import lombok.Setter;

@Getter
public class PaginationOptions {
  private static final Integer DEFAULT_LIMIT = 10;
  private static final Integer DEFAULT_OFFSET = 0;

  @Setter
  private Integer limit = DEFAULT_LIMIT;
  @Setter
  private Integer offset = DEFAULT_OFFSET;
}