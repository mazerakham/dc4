package dc4;

import bowser.model.Controller;
import bowser.model.Handler;
import bowser.template.Data;
import ox.Log;

public class DC4Controller extends Controller {

  @Override
  public void init() {
    route("GET", "/").to("home.html").data(data);
    route("GET", "/hello").to(helloHandler);
  }

  private final Data data = context -> {
    context.put("apiUrl", DC4Server.API_URL);
  };

  private final Handler helloHandler = (request, response) -> {
    String msg = "This hit the webserver and it needed to hit the API server.";
    Log.debug(msg);
    response.write(msg);
  };

}
