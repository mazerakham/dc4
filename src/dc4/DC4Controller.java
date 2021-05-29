package dc4;

import static com.google.common.base.Preconditions.checkNotNull;

import bowser.model.Controller;
import bowser.template.Data;
import dc4.model.User;
import ox.Log;

public class DC4Controller extends Controller {

  @Override
  public void init() {
    route("GET", "/").to("home.html").data(data);
  }

  private final Data data = context -> {
    User user = checkNotNull(context.request.get("user"));
    Log.debug("We have a user: " + user);
    context.put("apiUrl", DC4Server.API_URL);
  };

}
