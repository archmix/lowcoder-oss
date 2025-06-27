package lowcoder.sql.interfaces;

import lowcoder.metadata.interfaces.Table;
import lowcoder.sql.infra.PaginationType;
import lowcoder.testsuite.infra.RoutingContextMockBuilder;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class SearchOptionsTest {
  @Test
  public void givenRoutingContextAndPaginationTypeWhenGetSQLThenReturnsProperSQL() {
    //given
    var context = RoutingContextMockBuilder.create().build();
    var searchOptions = SearchOptions.create(table());

    searchOptions.from(context);

    //when
    var sqlOffSet = searchOptions.getSQL(PaginationType.OFFSET_FETCH);
    //then
    assertTrue(sqlOffSet.endsWith(PaginationType.OFFSET_FETCH.getStatement()));

    //when
    var sqlLimit = searchOptions.getSQL(PaginationType.LIMIT_OFFSET);
    //then
    assertTrue(sqlLimit.endsWith(PaginationType.LIMIT_OFFSET.getStatement()));
  }

  @Test
  public void givenRoutingContextWithSortWhenGetSQLThenReturnsProperSQL() {
    //given
    var context = RoutingContextMockBuilder.create()
      .withParams("sort", "-field1")
      .build();

    var table = table();

    var searchOptions = SearchOptions.create(table);
    searchOptions.from(context);

    //when
    var sql = searchOptions.getSQL(PaginationType.LIMIT_OFFSET);

    //then
    assertTrue(sql.contains("ORDER BY field1 DESC"));
  }


  @Test
  public void givenRoutingContextWithFieldsWhenGetSQLThenReturnsOnlyGivenFieldsInSQL() {
    //given
    var context = RoutingContextMockBuilder.create()
      .withParams("fields", "field1")
      .withParams("fields", "field2")
      .withParams("fields", "field3")
      .build();

    var table = table();

    //when
    var searchOptions = SearchOptions.create(table);
    searchOptions.from(context);
    var sql = searchOptions.getSQL(PaginationType.OFFSET_FETCH);

    //then
    assertTrue(sql.contains("field1"));
    assertTrue(sql.contains("field2"));
    assertTrue(sql.contains("field3"));
    assertFalse(sql.contains("field4"));
    assertFalse(sql.contains("field5"));
    assertFalse(sql.contains("field6"));
    assertTrue(sql.contains("OFFSET ? ROWS FETCH NEXT ? ROWS ONLY"));
  }

  @Test
  public void givenRoutingContextWithFiltersWhenGetSQLThenReturnsProperSQL() {
    //given
    var context = RoutingContextMockBuilder.create()
      .withParams("field1[gt]", "value1")
      .withParams("field2[like]", "value2")
      .withParams("field3[in]", "a,b")
      .withParams("_or", "field4[gte]:value4,field5[lte]:value5,field6[isNull]")
      .build();

    var table = table();

    var searchOptions = SearchOptions.create(table);
    searchOptions.from(context);

    //when
    var sql = searchOptions.getSQL(PaginationType.LIMIT_OFFSET);

    //then
    assertTrue(sql.contains("AND (field1 > ?"));
    assertTrue(sql.contains("AND field2 LIKE ?"));
    assertTrue(sql.contains("AND field3 IN (?,?))"));
    assertTrue(sql.contains("OR (field4 >= ?"));
    assertTrue(sql.contains("OR field5 <= ?"));
    assertTrue(sql.contains("OR field6 IS NULL)"));
  }

  private Table table() {
    return Table.builder().name("table1")
      .withColumn("id").asPrimaryKey("pk_table1")
      .withColumn("field1").add()
      .withColumn("field2").add()
      .withColumn("field3").add()
      .withColumn("field4").add()
      .withColumn("field5").add()
      .withColumn("field6").add()
      .build();
  }

}
