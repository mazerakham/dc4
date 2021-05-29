package dc4.db.upgrade.step;

import dc4.db.upgrade.UpgradeStep;
import ez.DB;

public class Step2 extends UpgradeStep {

  @Override
  public void upgrade(DB db) {
    // Intentionally empty. The version got messed up in step 1 somehow.

  }

}
