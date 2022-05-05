package databaseMetadataTest;

import legolas.mysql.interfaces.MySQLEntry;
import legolas.mysql.interfaces.MySQLServiceId;
import legolas.sql.interfaces.DatasourceFactory;
import lowcoder.metadata.infra.DatabaseMetadata;
import legolas.async.api.interfaces.Promise;
import legolas.config.api.interfaces.Configuration;
import legolas.postgre.interfaces.PostgreSQLEntry;
import legolas.postgre.interfaces.PostgreSQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import legolas.runtime.core.interfaces.RuntimeEnvironment;
import lowcoder.metadata.interfaces.*;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.Optional;
import java.util.concurrent.Executors;

public class DatabaseMetadataTest {
  private static RunningEnvironment environment;

  @BeforeClass
  public static void create() {
    Promise<RunningEnvironment> promise = RuntimeEnvironment.TEST.start(Executors.newSingleThreadExecutor());
    environment = promise.get();
  }

  @Test
  public void givenPostgresWithTablesWhenGetTablesThenReturns() {
    Configuration config = environment.get(PostgreSQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(PostgreSQLEntry.URL).get();
    String driver = config.getString(PostgreSQLEntry.DRIVER).get();
    String username = config.getString(PostgreSQLEntry.USERNAME).get();
    String password = config.getString(PostgreSQLEntry.PASSWORD).get();

    Connection connection = openConnection(url, driver, username, password);
    Tables tables = DatabaseMetadata.create(connection).getTables(DatabaseCatalog.empty(), DatabaseSchema.create("public"));

    this.doAssertion(tables);
  }

  @Test
  public void givenMySQLWithTablesWhenGetTablesThenReturns() {
    Configuration config = environment.get(MySQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(MySQLEntry.URL).get();
    String driver = config.getString(MySQLEntry.DRIVER).get();
    String username = config.getString(MySQLEntry.USERNAME).get();
    String password = config.getString(MySQLEntry.PASSWORD).get();

    Connection connection = openConnection(url, driver, username, password);
    Tables tables = DatabaseMetadata.create(connection).getTables(DatabaseCatalog.empty(), DatabaseSchema.create(username));

    this.doAssertion(tables);
  }

  private void doAssertion(Tables tables) {

    Table persons = tables.get("persons");
    Column id = persons.getPrimaryKeys().get(0);
    Assert.assertEquals(id.getType(), Column.ColumnType.INTEGER);
    Assert.assertFalse(id.getConstraints().get(ConstraintType.NULLABLE));

    Optional<Column> lastname = persons.getColumn("lastname");
    Assert.assertTrue(lastname.isPresent());
    Assert.assertEquals(lastname.get().getType(), Column.ColumnType.VARCHAR);
    Assert.assertFalse(lastname.get().getConstraints().get(ConstraintType.NULLABLE));

    Optional<Column> firstname = persons.getColumn("firstname");
    Assert.assertTrue(firstname.isPresent());
    Assert.assertEquals(firstname.get().getType(), Column.ColumnType.VARCHAR);
    Assert.assertTrue(firstname.get().getConstraints().get(ConstraintType.NULLABLE));

    Table orders = tables.get("orders");
    Assert.assertEquals(2, orders.getForeignKeys().size());

    Column personId = orders.getColumn("person_id").get();
    Assert.assertEquals(personId.getType(), Column.ColumnType.INTEGER);

    ForeignKey fk = personId.getConstraints().get(ConstraintType.FOREIGN_KEY);
    Assert.assertEquals(fk.getColumn().getName(), id.getName());
    Assert.assertEquals(fk.getIndexName().toString(), "fk_person_order");
    Assert.assertTrue(isRestrictOrNoAction(fk.getOnDelete()));
    Assert.assertEquals(ForeignKey.Rule.SET_NULL, fk.getOnUpdate());

    Column personId2 = orders.getColumn("person_id2").get();
    Assert.assertEquals(personId2.getType(), Column.ColumnType.INTEGER);

    ForeignKey fk2 = personId2.getConstraints().get(ConstraintType.FOREIGN_KEY);
    Assert.assertEquals(fk2.getColumn().getName(), id.getName());
    Assert.assertEquals(fk2.getIndexName().toString(), "fk_person_order2");
    Assert.assertEquals(ForeignKey.Rule.CASCADE, fk2.getOnDelete());
    Assert.assertTrue(isRestrictOrNoAction(fk.getOnDelete()));
  }

  private boolean isRestrictOrNoAction(ForeignKey.Rule rule){
    return ForeignKey.Rule.RESTRICT == rule || ForeignKey.Rule.NO_ACTION == rule;
  }

  private static Connection openConnection(String url, String driver, String username, String password){
    try {
      DataSource datasource = DatasourceFactory.toDataSource(url, driver, username, password);
      return DriverManager.getConnection(url, username, password);
    } catch (Exception e) {
      throw new IllegalStateException(e);
    }
  }
}
