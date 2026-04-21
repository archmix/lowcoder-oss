package lowcoder.sql.infra;

import morphos.api.interfaces.Field;
import morphos.api.interfaces.PrimaryKey;
import morphos.api.interfaces.Table;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import static lowcoder.testsuite.infra.SqlValidator.*;
import static org.junit.jupiter.api.Assertions.*;

public class UpdateSqlHandlerTest {
  @Test
  public void givenTableWithPrimaryKeyWhenGetSqlThenReturnsUpdateSql(){
    var table = table();
    var fieldNames = table.getFields().stream().map(Field::getName).collect(Collectors.toList());

    var sql = UpdateSqlHandler.of(table).getSql(fieldNames);
    assertTrue(isValidUpdate(sql));
    assertTrue(sql.contains("UPDATE table1 SET "));
    assertTrue(sql.contains("WHERE 1 = 1  AND id=?"));
    assertEquals(fieldNames.size() - 1, fieldsFromSql(sql).length);
  }

  @Test
  public void givenTableWithPrimaryKeyAndFieldNamesWhenGetSqlThenReturnsUpdateSql(){
    var table = table();
    var fieldNames = List.of("field1", "field2", "field3", "field4", "field5");
    var sql = UpdateSqlHandler.of(table).getSql(fieldNames);

    assertTrue(isValidUpdate(sql));
    assertTrue(sql.contains("UPDATE table1 SET "));
    assertTrue(sql.contains("WHERE 1 = 1  AND id=?"));
    assertEquals(fieldNames.size(), fieldsFromSql(sql).length);
  }

  private String[] fieldsFromSql(String sql) {
    var pattern = Pattern.compile(
      "SET\\s+(.*?)\\s+WHERE",
      Pattern.CASE_INSENSITIVE
    );

    var matcher = pattern.matcher(sql);
    if(!matcher.find()){
      return new String[0];
    }

    String inside = matcher.group(1);
    return inside.split(",");
  }

  private Table table() {
    return Table.builder().name("table1")
      .withColumn("id").generated(false).autoIncrement(false).asPrimaryKey("pk_table1")
      .withColumn("field1").add()
      .withColumn("field2").add()
      .withColumn("field3").add()
      .withColumn("field4").add()
      .withColumn("field5").add()
      .withColumn("field6").add()
      .build();
  }
}
