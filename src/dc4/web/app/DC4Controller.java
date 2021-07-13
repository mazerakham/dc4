package dc4.web.app;

import static com.google.common.base.Preconditions.checkNotNull;

import bowser.model.Controller;
import bowser.template.Data;
import dc4.CookieManager;
import dc4.DC4Server;
import dc4.model.User;

public class DC4Controller extends Controller {

  @Override
  public void init() {
    route("GET", "/").to("app.html").data(data);
  }

  private final Data data = context -> {
    User user = checkNotNull(context.request.get("user"));
    context.put("apiUrl", DC4Server.API_URL);
    context.put("websocketsUrl", DC4Server.WEBSOCKETS_URL);
    context.put("userToken", CookieManager.getHostCookie(context.request, "token"));
  };

}
