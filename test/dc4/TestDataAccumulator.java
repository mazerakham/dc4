package dc4;

import static com.google.common.base.Preconditions.checkNotNull;

import java.time.Instant;
import java.util.function.Consumer;

import org.junit.platform.engine.TestExecutionResult;
import org.junit.platform.launcher.TestExecutionListener;
import org.junit.platform.launcher.TestIdentifier;
import org.junit.platform.launcher.TestPlan;

import com.google.common.collect.Multimap;

import ox.Json;
import ox.Log;
import ox.x.XList;
import ox.x.XMap;
import ox.x.XMultimap;

public class TestDataAccumulator implements TestExecutionListener {

  private static final boolean verbose = false;

  private final XMap<String, TestData> testData = XMap.create();
  private final TestPlan testPlan;

  public TestDataAccumulator(TestPlan testPlan) {
    this.testPlan = testPlan;
  }

  @Override
  public void executionStarted(TestIdentifier t) {
    testData.put(t.getUniqueId(), new TestData(t));
    if (!t.getParentId().isPresent()) {
      return;
    }
    if (t.isContainer()) {
      Log.debug(t.getDisplayName());
    }
  }

  @Override
  public void executionFinished(TestIdentifier t, TestExecutionResult result) {
    if (verbose) {
      result.getThrowable().ifPresent(e -> e.printStackTrace());
    }
    TestData data = testData.get(t.getUniqueId());
    checkNotNull(data, "Could not find testdata: " + t.getUniqueId());
    data.endTime = Instant.now();
    data.result = result;

    if (!t.getParentId().isPresent()) {
      return;
    }

    if (t.isTest()) {
      Log.debug("\t" + t.getDisplayName() + "\t" + result.getStatus());
    }
  }

  public Json toJson() {
    Json ret = Json.object();

    XList<TestData> tests = testData.values().filter(td -> td.testId.isTest());

    walk(testPlan, node -> {
      if (node.isTest()) {
        if (!testData.containsKey(node.getUniqueId())) {
          if (testData.containsKey(node.getParentId().get())) {
            // this test never ran
            tests.add(new TestData(node));
          } else {
            // this whole file did not run
            Log.warn("Did not run: " + node);
          }
        }
      }
    });

    ret.with("passedTests", tests.filter(TestData::passed).size());
    ret.with("totalTests", tests.size());

    Multimap<TestData, TestData> containerTests = XMultimap.create();
    tests.forEach(test -> {
      TestData container = testData.get(test.testId.getParentId().get());
      containerTests.put(container, test);
    });

    ret.with("containers", Json.array(containerTests.keySet(), container -> {
      return container.toJson()
          .with("tests", Json.array(containerTests.get(container), TestData::toJson));
    }));

    return ret;
  }

  private static void walk(TestPlan plan, Consumer<TestIdentifier> callback) {
    plan.getRoots().forEach(root -> walk(plan, root, callback));
  }

  private static void walk(TestPlan plan, TestIdentifier node, Consumer<TestIdentifier> callback) {
    callback.accept(node);
    plan.getChildren(node).forEach(child -> walk(plan, child, callback));
  }

  private static class TestData {
    public final TestIdentifier testId;
    public Instant startTime = Instant.now(), endTime;
    public TestExecutionResult result = null;

    public TestData(TestIdentifier testId) {
      this.testId = testId;
    }

    public Json toJson() {
      Json ret = Json.object()
          .with("name", testId.getDisplayName());
      if (result == null) {
        ret.with("status", "NOT_RUN");
      } else {
        ret.with("startTime", startTime)
            .with("endTime", endTime)
            .with("status", result.getStatus());
        result.getThrowable().ifPresent(t -> {
          Json trace = Json.array();
          trace.add(t.toString());
          for (StackTraceElement e : t.getStackTrace()) {
            trace.add(e.toString());
          }
          ret.with("stackTrace", trace);
        });
      }
      return ret;
    }

    public boolean passed() {
      return result != null && result.getStatus() == TestExecutionResult.Status.SUCCESSFUL;
    }

    @Override
    public String toString() {
      return testId.getDisplayName();
    }
  }

}
