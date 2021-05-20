package fabelexample;

import bowser.model.Controller;

public class FabelExampleController extends Controller {

  @Override
  public void init() {
    route("GET", "/").to("home.html");
  }

}
