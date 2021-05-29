package dc4.db.upgrade.step;

import dc4.db.upgrade.UpgradeStep;
import ez.DB;
import ox.Log;

public class Step3 extends UpgradeStep {

  @Override
  public void upgrade(DB db) {
    try {
      db.deleteColumn("user", "token");
    } catch (Exception e) {
      Log.info("Column already deleted.");
    }
  }

}
