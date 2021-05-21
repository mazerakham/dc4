package dc4.db;

import ez.Row;
import ez.Table;

public class KVDB extends DC4DB {

  @Override
  protected Table getTable() {
    return new Table("kv")
        .column("key", String.class).uniqueIndex()
        .column("val", String.class);
  }

  public Integer getInt(String key) {
    Row row = rawQuery().selectSingleRow("SELECT * FROM `kv` WHERE `key` = ?", key);
    return row == null ? null : Integer.parseInt(row.get("val"));
  }

  public void put(String key, Object val) {
    Row row = rawQuery().selectSingleRow("SELECT * FROM `kv` WHERE `key` = ?", key);
    if (row == null) {
      rawQuery().insert("kv", new Row().with("key", key).with("val", val));
    } else {
      rawQuery().update("UPDATE `kv` SET `val` = ? WHERE `key` = ?", val, key);
    }
  }
}
