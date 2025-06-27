package lowcoder.sql.interfaces;

import lowcoder.metadata.interfaces.Table;
import org.junit.Test;

import static org.junit.Assert.*;

public class NamedFilterTest {

  @Test
  public void givenFieldAndPredicateAndValueWhenParseThenReturnNamedFilter(){
    var table = table();

    var namedFilter = NamedFilter.create("name[isNull]");
    var column = table.getColumn(namedFilter.getName()).get();

    assertEquals(namedFilter.getName(), column.getName());
    assertEquals(namedFilter.getPredicate(), Predicate.IS_NULL);
  }

  @Test
  public void givenArrayValueWhenParseThenReturnInPredicate(){
    var namedFilter = NamedFilter.create("name");
    var column = table().getColumn(namedFilter.getName()).get();

    assertEquals(namedFilter.getName(), column.getName());
    assertEquals(namedFilter.getPredicate(), Predicate.IN);
  }

  private Table table(){
    return Table.builder().name("table")
      .withColumn("name").add()
      .withColumn("age").add()
      .build();
  }
}
