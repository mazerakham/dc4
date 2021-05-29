package dc4.db;

import java.util.Set;

import dc4.model.AbstractModel;
import ez.DB;
import ez.Row;
import ez.Table;
import ox.Config;
import ox.Log;
import ox.Reflection;
import ox.x.XList;

public abstract class DC4DB<T extends AbstractModel> {

  private static final Config config = Config.load("dc4");

  public static DB db;

  public static Set<String> tables;

  private final Table table;

  @SuppressWarnings("unchecked")
  protected final Class<T> modelClass = (Class<T>) Reflection.getGenericClass(getClass());

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

  public T get(long id) {
    return getUnique("id", id);
  }

  public T insert(T t) {
    Row row = table.toRow(t);
    t.id = db.insert(table.name, row);
    return t;
  }

  public void update(long id, String column, Object val) {
    db.update("UPDATE `" + table.name + "` SET `" + column + "` = ? WHERE `id` = ?", val, id);
  }

  public T getUnique(String column, Object val) {
    return getResults("SELECT * FROM `" + table.name + "` WHERE `" + column + "` = ? LIMIT 1", val).only().get();
  }

  public XList<T> getIndexed(String column, Object val) {
    return getResults("SELECT * FROM `" + table.name + "` WHERE `" + column + "` = ?", val);
  }

  public XList<T> getResults(String query, Object... objs) {
    return db.select(query, objs).map(r -> table.fromRow(r, modelClass));
  }

  protected DB rawQuery() {
    return db;
  }
}
