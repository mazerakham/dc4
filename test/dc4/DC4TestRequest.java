package dc4;

import static ox.util.Utils.checkNotEmpty;
import static ox.util.Utils.normalize;

import ox.Json;
import ox.x.XMap;
import ox.x.XOptional;

/**
 * For now this represents an http request to the webserver (as opposed to the API server).  We'll generalize later.
 */
public class DC4TestRequest {

  public final String method;
  public final String path;
  private XOptional<Json> body = XOptional.empty();
  public final XMap<String, String> cookies = XMap.create();
  
  /**
   * E.g. new DC4TestRequest("GET", "/")
   */
  public DC4TestRequest(String method, String route) {
    this.method = checkNotEmpty(normalize(method));
    this.path = checkNotEmpty(normalize(route));
  }
  
  public DC4TestRequest withCookie(String key, String val) {
    cookies.put(checkNotEmpty(normalize(key)), checkNotEmpty(normalize(val)));
    return this;
  }
  
  public String getCookie(String key) {
    return cookies.get(key);
  }
  
  public String body() {
    return body.compute(j -> j.toString(), "");
  }
  
  public DC4TestRequest body(Json json) {
    this.body = XOptional.of(json);
    return this;
  }
}
