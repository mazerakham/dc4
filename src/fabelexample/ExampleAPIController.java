package fabelexample;

import bowser.model.Controller;
import bowser.model.Handler;
import ox.Json;

public class ExampleAPIController extends Controller {

  @Override
  public void init() {
    route("GET", "/hello").to(helloHandler);
  }

  private Handler helloHandler = (request, response) -> {
    response.write(Json.object().with("a", 42).with("hello", "world"));
  };

}
