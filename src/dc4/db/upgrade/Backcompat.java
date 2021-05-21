package dc4.db.upgrade;

import dc4.db.DC4DB;
import dc4.db.VersionDB;
import ox.Log;
import ox.Reflection;

public class Backcompat {
  private final VersionDB versionDB = new VersionDB();

  public void run() {
    run("dc4.db.upgrade.step");
  }

  @SuppressWarnings("unchecked")
  public void run(String upgradePackage) {
    int version = versionDB.getCurrentVersion();
    if (version == 0) {
      // this is a new database, so we need to set the initial version
      markCurrentVersion(0);
    }

    int stepsRun = 0;

    while (true) {
      try {
        Class<UpgradeStep> c = (Class<UpgradeStep>) Class.forName(upgradePackage + ".Step" + (version + 1));
        UpgradeStep step = Reflection.newInstance(c);
        Log.info("Upgrading from version " + version + " to version " + (version + 1));
        DC4DB.db.transaction(() -> step.upgrade(DC4DB.db));
        versionDB.incVersion();
        stepsRun++;
        version++;
      } catch (ClassNotFoundException e) {
        break;
      }
    }

    if (stepsRun > 0) {
      DC4DB.tables.clear();
      DC4DB.tables.addAll(DC4DB.db.getTables());
    }
  }

  private void markCurrentVersion(int version) {
    versionDB.setVersion(version);
  }
}
