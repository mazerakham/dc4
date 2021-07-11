package dc4;

import org.junit.jupiter.engine.JupiterTestEngine;
import org.junit.platform.engine.discovery.ClassNameFilter;
import org.junit.platform.engine.discovery.ClassSelector;
import org.junit.platform.engine.discovery.DiscoverySelectors;
import org.junit.platform.launcher.Launcher;
import org.junit.platform.launcher.LauncherDiscoveryRequest;
import org.junit.platform.launcher.TestPlan;
import org.junit.platform.launcher.core.LauncherConfig;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder;
import org.junit.platform.launcher.core.LauncherFactory;

import com.google.common.base.Stopwatch;

import dc4.arch.ReflectionUtils;
import ox.File;
import ox.IO;
import ox.Json;
import ox.Log;
import ox.util.Time;
import ox.x.XList;

/**
 * This is meant to be run on a testing server. It runs all the tests and saves a file with the results.
 */
public class DC4TestExecutor {

  private final File testResultsFolder = File.home("test-results").mkdirs();

  public void runAllTests() {
    Stopwatch watch = Stopwatch.createStarted();
    XList<ClassSelector> selectors = ReflectionUtils.findTestClasses().map(c -> DiscoverySelectors.selectClass(c));
    Log.debug("Found " + selectors.size() + " Test classes (" + watch + ")");
    // Log.debug(selectors);

    LauncherDiscoveryRequest request = LauncherDiscoveryRequestBuilder.request()
        .selectors(selectors)
        // .selectors(DiscoverySelectors.selectPackage("com.ender"), DiscoverySelectors.selectPackage("ender"))
        .filters(ClassNameFilter.includeClassNamePatterns(".*Test"))
        .configurationParameter("junit.jupiter.execution.parallel.enabled", "true")
        .build();

    Launcher launcher = LauncherFactory.create(LauncherConfig.builder()
        .enableTestEngineAutoRegistration(false)
        .addTestEngines(new JupiterTestEngine())
        .build());

    TestPlan plan = launcher.discover(request);

    // XList<String> list = XList.create();
    // plan.getChildren(plan.getRoots().iterator().next()).forEach(t -> {
    // list.add(t.getDisplayName());
    // });
    // list.sortSelf().log();

    TestDataAccumulator accumulator = new TestDataAccumulator(plan);
    // launcher.execute(request, accumulator);
    launcher.execute(plan, accumulator);

    File file = testResultsFolder.child(System.currentTimeMillis() + ".json");
    Log.info("All tests complete. Saving results to " + file);

    Json results = accumulator.toJson();
    IO.from(results.prettyPrint()).to(file);
    Log.info("Saved.");

    int passed = results.getInt("passedTests"), total = results.getInt("totalTests");
    if (passed == total) {
      Log.info("Passed %d / %d tests.", passed, total);
    } else {
      Log.error("Only passed " + passed + " / " + total + " tests.");
    }
  }

  public static void main(String[] args) {
    try {
      Log.logToFolder("dc4");
      Time.setDefaultTimeZone(Time.CENTRAL);
      new DC4TestExecutor().runAllTests();
    } catch (Exception e) {
      e.printStackTrace();
    } finally {
      System.exit(0);
    }
  }

}
