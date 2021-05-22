package dc4.db;

import java.util.Set;

import ez.DB;
import ez.Table;
import ox.Config;
import ox.Log;

public abstract class DC4DB {

  private static final Config config = Config.load("dc4");

  public static DB db;

  public static Set<String> tables;

  private final Table table;

  protected abstract Table getTable();

  public DC4DB() {
    table = getTable();
    if (tables == null) {
      connectToDatabase();
    }
    if (!tables.contains(table.name.toLowerCase())) {
      Log.info("Creating table: " + table);
      db.addTable(table);
      tables.add(table.name.toLowerCase());
    }
  }

  public static void connectToDatabase() {
    String schema = config.get("mysql.schema", "dc4");
    connectToDatabase(schema);
  }

  public static synchronized void connectToDatabase(String schema) {
    if (db != null) {
      return;
    }

    String ip = config.get("mysql.ip", "localhost");
    String user = config.get("mysql.user", "root");
    Log.debug("Connecting to database: " + ip + ":" + schema);
    db = new DB(ip, user, config.get("mysql.password", ""), schema, false, config.getInt("mysql.maxConnections", 4))
        .ensureSchemaExists();
    tables = db.getTables();
  }

  protected DB rawQuery() {
    return db;
  }
}
