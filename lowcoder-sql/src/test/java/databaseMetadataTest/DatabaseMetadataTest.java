package databaseMetadataTest;

import legolas.mysql.interfaces.MySQLEntry;
import legolas.mysql.interfaces.MySQLServiceId;
import lowcoder.metadata.infra.DatabaseMetadata;
import legolas.config.api.interfaces.Configuration;
import legolas.postgre.interfaces.PostgreSQLEntry;
import legolas.postgre.interfaces.PostgreSQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import legolas.runtime.core.interfaces.RuntimeEnvironment;
import lowcoder.metadata.interfaces.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.concurrent.Executors;

public class DatabaseMetadataTest {
  private static RunningEnvironment environment;

  @BeforeClass
  public static void create() {
    environment = RuntimeEnvironment.TEST.start(Executors.newSingleThreadExecutor()).get();
  }

  @Test
  public void givenPostgresWithTablesWhenGetTablesThenReturns() {
    Configuration config = environment.get(PostgreSQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(PostgreSQLEntry.URL).get();
    String driver = config.getString(PostgreSQLEntry.DRIVER).get();
    String username = config.getString(PostgreSQLEntry.USERNAME).get();
    String password = config.getString(PostgreSQLEntry.PASSWORD).get();

    Connection connection = openConnection(url, driver, username, password);
    DatabaseMetadata.create(connection).loadTables(DatabaseCatalog.empty(), DatabaseSchema.create("public"), this::doAssertion);
  }

  @Test
  public void givenMySQLWithTablesWhenGetTablesThenReturns() {
    Configuration config = environment.get(MySQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(MySQLEntry.URL).get();
    String driver = config.getString(MySQLEntry.DRIVER).get();
    String username = config.getString(MySQLEntry.USERNAME).get();
    String password = config.getString(MySQLEntry.PASSWORD).get();

    Connection connection = openConnection(url, driver, username, password);
    DatabaseMetadata.create(connection).loadTables(DatabaseCatalog.empty(), DatabaseSchema.create(username), this::doAssertion);
  }

  private void doAssertion(Table table) {
    if(table.getName().equalsIgnoreCase("migrami_snapshot")){
      return;
    }

    if(table.getName().equalsIgnoreCase("persons")) {
      assertPersonsTable(table);
      return;
    }

    if(table.getName().equalsIgnoreCase("orders")) {
      assertOrdersTable(table);
      return;
    }

    if(table.getName().equalsIgnoreCase("auto_generation_id")) {
      assertAutoGenerationId(table);
      return;
    }

    Assert.fail("Please update this test to reflect the database structure");
  }

  private void assertAutoGenerationId(Table table){
    PrimaryKey id = table.getPrimaryKeys().iterator().next();
    Assert.assertEquals(id.getColumn().getType(), Column.ColumnType.INTEGER);
    Assert.assertFalse(id.getColumn().getConstraints().get(ConstraintType.NULLABLE));
    Assert.assertTrue(id.getGenerated());
  }

  private void assertPersonsTable(Table table) {
    PrimaryKey id = table.getPrimaryKeys().iterator().next();
    Assert.assertEquals(id.getColumn().getType(), Column.ColumnType.INTEGER);
    Assert.assertFalse(id.getColumn().getConstraints().get(ConstraintType.NULLABLE));

    Optional<Column> lastname = table.getColumn("lastname");
    Assert.assertTrue(lastname.isPresent());
    Assert.assertEquals(lastname.get().getType(), Column.ColumnType.VARCHAR);
    Assert.assertFalse(lastname.get().getConstraints().get(ConstraintType.NULLABLE));

    Optional<Column> firstname = table.getColumn("firstname");
    Assert.assertTrue(firstname.isPresent());
    Assert.assertEquals(firstname.get().getType(), Column.ColumnType.VARCHAR);
    Assert.assertTrue(firstname.get().getConstraints().get(ConstraintType.NULLABLE));
  }

  private void assertOrdersTable(Table table) {
    Assert.assertEquals(2, table.getForeignKeys().size());

    ForeignKey personId = table.getForeignKey("person_id").get();
    Assert.assertEquals(personId.getColumn().getType(), Column.ColumnType.INTEGER);

    Assert.assertEquals(personId.getTableName(), "persons");
    Assert.assertEquals(personId.getColumn().getName(), "id");
    Assert.assertEquals(personId.getIndexName(), "fk_person_order");
    Assert.assertTrue(isRestrictOrNoAction(personId.getOnDelete()));
    Assert.assertEquals(ForeignKey.Rule.SET_NULL, personId.getOnUpdate());

    ForeignKey personId2 = table.getForeignKey("person_id2").get();
    Assert.assertEquals(personId2.getColumn().getType(), Column.ColumnType.INTEGER);

    Assert.assertEquals(personId2.getTableName(), "persons");
    Assert.assertEquals(personId2.getColumn().getName(), "id");
    Assert.assertEquals(personId2.getIndexName(), "fk_person_order2");
    Assert.assertEquals(ForeignKey.Rule.CASCADE, personId2.getOnDelete());
    Assert.assertTrue(isRestrictOrNoAction(personId2.getOnDelete()));
  }

  private boolean isRestrictOrNoAction(ForeignKey.Rule rule){
    return ForeignKey.Rule.RESTRICT == rule || ForeignKey.Rule.NO_ACTION == rule;
  }

  private static Connection openConnection(String url, String driver, String username, String password){
    try {
      return DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}