package dc4.db.upgrade;

import static com.google.common.base.Preconditions.checkState;
import static java.lang.Integer.parseInt;

import ez.DB;

public abstract class UpgradeStep {

  public abstract void upgrade(DB db);

  public final int getStepNumber() {
    String s = getClass().getSimpleName(); // Step123
    checkState(s.startsWith("Step"));
    return parseInt(s.substring(4));
  }
}
