package lowcoder.testsuite.infra;

import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.insert.Insert;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.update.Update;

public class SqlValidator {
  public static boolean isValidSelect(String sql) {
    try {
      return CCJSqlParserUtil.parse(sql) instanceof Select;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isValidInsert(String sql) {
    try {
      return CCJSqlParserUtil.parse(sql) instanceof Insert;
    } catch (Exception e) {
      return false;
    }
  }

  public static boolean isValidUpdate(String sql) {
    try {
      return CCJSqlParserUtil.parse(sql) instanceof Update;
    } catch (Exception e) {
      return false;
    }
  }
}
