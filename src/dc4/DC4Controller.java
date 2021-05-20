package dc4;

import bowser.model.Controller;
import bowser.template.Data;

public class DC4Controller extends Controller {

  @Override
  public void init() {
    route("GET", "/").to("home.html").data(data);
  }

  private final Data data = context -> {
    context.put("apiUrl", DC4Server.API_URL);
  };

}
