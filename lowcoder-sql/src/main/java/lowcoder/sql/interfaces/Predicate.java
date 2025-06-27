package lowcoder.sql.interfaces;

public enum Predicate {
  EQUALS("", "="),
  GREATER_THAN("gt", ">"),
  LESS_THAN("lt","<"),
  GREATER_THAN_OR_EQUALS("gte",">="),
  LESS_THAN_OR_EQUALS("lte","<="),
  NOT_EQUALS("not","!="),
  IN("in", "IN"),
  LIKE("like","LIKE"),
  NOT_LIKE("notLike", "NOT LIKE"),
  IS_NULL("isNull", "IS NULL"),
  IS_NOT_NULL("isNotNull", "IS NOT NULL");

  private final String suffix;
  private final String keyword;

  Predicate(String prefix, String keyword) {
    this.suffix = prefix;
    this.keyword = keyword;
  }

  public String keyword() {
    return keyword;
  }

  public String suffix() {
    return suffix;
  }

  public static Predicate of(String predicate) {
    for(Predicate p : Predicate.values()) {
      if(p.suffix().equals(predicate)) {
        return p;
      }
    }

    throw new IllegalArgumentException("Predicate not found: " + predicate);
  }
}