package dc4;

import bowser.model.Controller;

public class DC4Controller extends Controller {

  @Override
  public void init() {
    route("GET", "/").to("home.html");
  }

}
