package dc4;

import static com.google.common.base.Preconditions.checkState;

import bowser.model.Controller;
import bowser.model.Handler;
import bowser.model.Request;
import dc4.db.KVDB;
import dc4.model.User;
import ox.Json;

public class DC4APIController extends Controller {

  private final KVDB kv = new KVDB();

  @Override
  public void init() {
    route("GET", "/hello").to(helloHandler);
    route("GET", "/counter").to(getCounter);
    route("POST", "/counter").to(incrementCounter);
    route("GET", "/personalCounter").to(getPersonalCounter);
  }

  private Handler helloHandler = (request, response) -> {
    checkLoggedIn(request);
    response.write(Json.object().with("a", 42).with("hello", "world"));
  };

  private Handler getCounter = (request, response) -> {
    checkLoggedIn(request);
    response.write(Json.object().with("count", kv.getInt("count")));
  };

  private Handler incrementCounter = (request, response) -> {
    checkLoggedIn(request);
    User user = request.get("user");
    int newCount = kv.getInt("count") + 1;
    kv.put("count", newCount);
    response.write(Json.object().with("newCount", newCount));
  };

  private Handler getPersonalCounter = (request, response) -> {

  };

  private void checkLoggedIn(Request request) {
    checkState(request.get("user") != null);
  }

}
