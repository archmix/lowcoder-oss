package lowcoder.sql.interfaces;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@Getter
public class PaginationOptions {
  private static final Long DEFAULT_LIMIT = 10L;
  private static final Long DEFAULT_OFFSET = 0L;

  @Setter
  private Long limit = DEFAULT_LIMIT;
  @Setter
  private Long offset = DEFAULT_OFFSET;

  public static PaginationOptions create() {
    return new PaginationOptions();
  }
}