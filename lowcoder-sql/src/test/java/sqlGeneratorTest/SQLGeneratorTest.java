package sqlGeneratorTest;

import lowcoder.sql.interfaces.SQLGenerator;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class SQLGeneratorTest {

  @Test
  public void givenTableAndColumnsThenGenerateInsertSQL() {
    String table = "table";
    List<String> columns = Arrays.asList("c1", "c2", "c3");

    String generatedSQL = SQLGenerator.create().insertSQL(table, columns);
    Assert.assertEquals("INSERT INTO table(c1,c2,c3) VALUES (?,?,?)", generatedSQL);
  }

  @Test
  public void givenTableThenGenerateDeleteSQL() {
    String table = "table";

    String generatedSQL = SQLGenerator.create().deleteSQL(table);
    Assert.assertEquals("DELETE FROM table", generatedSQL);
  }

  @Test
  public void givenTableAndConstraintThenGenerateDeleteSQL() {
    String table = "table";

    String generatedSQL = SQLGenerator.create().deleteBySQL(table, "c1");
    Assert.assertEquals("DELETE FROM table WHERE c1 = ?", generatedSQL);
  }

  @Test
  public void givenTableAndConstraintsThenGenerateDeleteSQL() {
    String table = "table";
    List<String> constraints = List.of("c1");

    SQLGenerator sqlGenerator = SQLGenerator.create();
    String generatedSQL = sqlGenerator.deleteSQL(table, constraints);
    Assert.assertEquals("DELETE FROM table WHERE c1 = ?", generatedSQL);

    constraints = Arrays.asList("c1","c2");
    generatedSQL = sqlGenerator.deleteSQL(table, constraints);
    Assert.assertEquals("DELETE FROM table WHERE c1 = ? AND c2 = ?", generatedSQL);
  }

  @Test
  public void givenTableAndColumnsAndConstraintsThenGenerateUpdateSQL() {
    String table = "table";
    List<String> columns = Arrays.asList("c1", "c2", "c3");
    List<String> constraints = List.of("c4");

    SQLGenerator sqlGenerator = SQLGenerator.create();

    String generatedSQL = sqlGenerator.updateSQL(table, columns, constraints);
    Assert.assertEquals("UPDATE table SET c1 = ?,c2 = ?,c3 = ? WHERE c4 = ?", generatedSQL);

    constraints = Arrays.asList("c4", "c5");
    generatedSQL = sqlGenerator.updateSQL(table, columns, constraints);
    Assert.assertEquals("UPDATE table SET c1 = ?,c2 = ?,c3 = ? WHERE c4 = ? AND c5 = ?", generatedSQL);
  }

  @Test
  public void givenTableAndColumnsThenGenerateSelectAllSQL() {
    String table = "table";
    List<String> columns = Arrays.asList("c1", "c2", "c3");

    String generatedSQL = SQLGenerator.create().selectAllSQL(table, columns);
    Assert.assertEquals("SELECT c1,c2,c3 FROM table", generatedSQL);
  }

  @Test
  public void givenTableAndColumnsAndConstraintsThenGenerateSelectSQL() {
    String table = "table";
    List<String> columns = Arrays.asList("c1", "c2", "c3");
    List<String> constraints = Arrays.asList("c4", "c5", "c6");

    String generatedSQL = SQLGenerator.create().selectSQL(table, columns, constraints);
    Assert.assertEquals("SELECT c1,c2,c3 FROM table WHERE c4 = ? AND c5 = ? AND c6 = ?", generatedSQL);
  }

  @Test
  public void givenTableAndColumnsAndConstraintsThenGenerateSelectBySQL() {
    String table = "table";
    List<String> columns = Arrays.asList("c1", "c2", "c3");
    List<String> constraints = Arrays.asList("c4", "c5", "c6");

    String generatedSQL = SQLGenerator.create().selectBySQL(table, columns, "c4");
    Assert.assertEquals("SELECT c1,c2,c3 FROM table WHERE c4 = ?", generatedSQL);
  }
}
