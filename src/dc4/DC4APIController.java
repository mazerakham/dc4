package dc4;

import bowser.model.Controller;
import bowser.model.Handler;
import dc4.db.KVDB;
import ox.Json;

public class DC4APIController extends Controller {

  private final KVDB kv = new KVDB();

  @Override
  public void init() {
    route("GET", "/hello").to(helloHandler);
    route("GET", "/counter").to(getCounter);
    route("POST", "/counter").to(incrementCounter);
  }

  private Handler helloHandler = (request, response) -> {
    response.write(Json.object().with("a", 42).with("hello", "world"));
  };

  private Handler getCounter = (request, response) -> {
    response.write(Json.object().with("count", kv.getInt("count")));
  };

  private Handler incrementCounter = (request, response) -> {
    int newCount = kv.getInt("count") + 1;
    kv.put("count", newCount);
    response.write(Json.object().with("newCount", newCount));
  };

}
