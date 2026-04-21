package lowcoder.sql.infra;

import morphos.api.interfaces.Table;
import org.junit.jupiter.api.Test;

import java.util.regex.Pattern;

import static lowcoder.testsuite.infra.SqlValidator.*;
import static org.junit.jupiter.api.Assertions.*;

public class InsertSqlHandlerTest {
  @Test
  public void givenTableWithGeneratedPrimaryKeyWhenGetSqlThenReturnsInsertSql(){
    var table = table(true);
    var sql = InsertSqlHandler.of(table).getSql();
    var fields = fieldsFromSql(sql);

    assertTrue(isValidInsert(sql));
    assertEquals(6, fields.length);
    assertTrue(sql.contains("INSERT INTO table1("));
    assertTrue(sql.contains("VALUES (?,?,?,?,?,?)"));
  }

  @Test
    public void givenTableWithPrimaryKeyWhenGetSqlThenReturnsInsertSql(){
    var table = table(false);
    var sql = InsertSqlHandler.of(table).getSql();
    var fields = fieldsFromSql(sql);

    assertTrue(isValidInsert(sql));
    assertEquals(7, fields.length);
    assertTrue(sql.contains("INSERT INTO table1("));
    assertTrue(sql.contains("VALUES (?,?,?,?,?,?,?)"));
  }

  private String[] fieldsFromSql(String sql) {
    var pattern = Pattern.compile("\\(([^)]*)\\)");
    var matcher = pattern.matcher(sql);

    if(!matcher.find()){
      return new String[0];
    }

    String inside = matcher.group(1);
    return inside.split(",");
  }

  private Table table(boolean generatedPk) {
    return Table.builder().name("table1")
      .withColumn("id").generated(generatedPk).autoIncrement(false).asPrimaryKey("pk_table1")
      .withColumn("field1").add()
      .withColumn("field2").add()
      .withColumn("field3").add()
      .withColumn("field4").add()
      .withColumn("field5").add()
      .withColumn("field6").add()
      .build();
  }
}
