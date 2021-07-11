package dc4.db;

import static com.google.common.base.Preconditions.checkNotNull;
import static ox.util.Utils.propagate;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collection;

import org.h2.jdbcx.JdbcConnectionPool;

import ez.DB;
import ez.Table;
import ez.Table.Index;

public class InMemoryDB extends DB {

  private final String url;
  private final JdbcConnectionPool pool;
  private boolean initialized = false;

  public InMemoryDB(String schema) {
    super(schema);

    url = "jdbc:h2:mem:" + schema + ";MODE=MySQL;DATABASE_TO_UPPER=false;DB_CLOSE_DELAY=-1";
    pool = JdbcConnectionPool.create(url, "abc", "abc");

    createSchema(schema);
    initialized = true;
  }

  @Override
  public Connection getConnection() {
    Connection ret = transactionConnections.get();
    if (ret != null) {
      return ret;
    }
    try {
      Connection conn = pool.getConnection();
      if (initialized) {
        conn.setSchema(schema);
      }
      return conn;
    } catch (SQLException e) {
      throw propagate(e);
    }
  }

  @Override
  public void createSchema(String schema) {
    execute("CREATE SCHEMA `" + schema + "`");
  }

  @Override
  public boolean addTable(Table table) {
    // Log.debug("ADDTABLE: " + table.name);
    // if (table.name.equals("application")) {
    // Thread.dumpStack();
    // }
    checkNotNull(table);

    if (getTables(true).contains(table.name)) {
      return false;
    }

    String sql = table.toSQL(schema);
    int i = sql.indexOf("ENGINE");
    sql = sql.substring(0, i);
    execute(sql);
    for (Index index : table.getIndices()) {
      addIndex(table.name, index.columns, index.unique);
    }

    return true;
  }

  @Override
  protected void addIndex(String table, Collection<String> columns, boolean unique, String indexName) {
    super.addIndex(table, columns, unique, table + "_" + indexName);
  }

}
