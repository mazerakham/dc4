package dc4.db.upgrade.step;

import dc4.db.KVDB;
import dc4.db.upgrade.UpgradeStep;
import ez.DB;

public class Step1 extends UpgradeStep {

  @Override
  public void upgrade(DB db) {
    new KVDB().put("count", 0);
  }

}
