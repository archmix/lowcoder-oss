package lowcoder.openapi.application;

import io.vertx.core.Vertx;
//import legolas.mysql.interfaces.MySQLEntry;
//import legolas.mysql.interfaces.MySQLServiceId;
import legolas.runtime.core.interfaces.RunningEnvironment;
import lowcoder.testsuite.application.DomainTests;
import org.junit.jupiter.api.BeforeAll;

public class MySQLTestSuite extends DomainTests {
  @BeforeAll
  public static void beforeAll(RunningEnvironment environment, Vertx vertx){
    setMySQLConfig(environment);
    startLowcoder(vertx);
  }

  private static void setMySQLConfig(RunningEnvironment environment) {
    /*
    var config = environment.get(MySQLServiceId.INSTANCE).get().configuration();

    String url = config.getString(MySQLEntry.URL).get();
    String driver = config.getString(MySQLEntry.DRIVER).get();
    String username = config.getString(MySQLEntry.USERNAME).get();
    String password = config.getString(MySQLEntry.PASSWORD).get();

    setDatabaseConfig("test", url, driver, username, password);
    */
  }
}
