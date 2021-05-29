package dc4;

import java.util.concurrent.TimeUnit;

import bowser.model.Request;
import bowser.model.Response;
import bowser.websocket.ClientSocket;
import ox.Config;

public class CookieManager {

  private static final Config config = Config.load("dc4");

  private static final String COOKIE_PREFIX = config.get("cookiePrefix", "localhost");

  /**
   * Get the cookie with name prefixed by the cookie domain.
   */
  public static String getHostCookie(Request request, String key) {
    return request.cookie(getKey(key));
  }

  public static String getHostCookie(ClientSocket clientSocket, String key) {
    return clientSocket.getCookies().get(getKey(key));
  }

  /**
   * Set the cookie with name prefixed by the cookie domain.
   */
  public static void setHostCookie(Response response, String key, String value, int expiry, TimeUnit units) {
    setHostCookie(response, key, value, expiry, units, "");
  }

  public static void setHostCookie(Response response, String key, String value, int expiry, TimeUnit units,
      String domain) {
    response.cookie(getKey(key), value, expiry, units, domain);
  }

  public static void removeHostCookie(Response response, String key) {
    response.removeCookie(getKey(key));
  }

  private static String getKey(String key) {
    return COOKIE_PREFIX + "." + key;
  }

}
