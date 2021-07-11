package dc4;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;

import org.simpleframework.http.Cookie;
import org.simpleframework.http.Path;
import org.simpleframework.http.Request;
import org.simpleframework.http.Response;
import org.simpleframework.http.core.Container;
import org.simpleframework.http.parse.AddressParser;

import bowser.WebServer;
import dc4.web.DC4Webserver;
import ox.Reflection;

public class DC4ServerTest {

  private DC4Webserver dc4Webserver;
  private WebServer webserver;
  private Container container; 
  
  public DC4ServerTest() {
    dc4Webserver = new DC4Webserver();
    webserver = Reflection.get(dc4Webserver, "webserver");
    container = Reflection.get(webserver, "container");
  }
 
  public CapturedResponse request(DC4TestRequest dc4Request) {
    try {
      Path path = mock(Path.class);
      when(path.getPath()).thenReturn(dc4Request.path);
      
      Request request = mock(Request.class);
      AddressParser addressParser = new AddressParser(dc4Request.path);
      when(request.getPath()).thenReturn(addressParser.getPath());
      when(request.getValue("Origin")).thenReturn("PLACEHOLDER");
      when(request.getMethod()).thenReturn(dc4Request.method);
      when(request.getQuery()).thenReturn(addressParser.getQuery());
      when(request.getContent()).thenReturn(dc4Request.body());
      
      Cookie tokenCookie = mock(Cookie.class);
      when(tokenCookie.getValue()).thenReturn(dc4Request.getCookie("token"));
      when(request.getCookie("localhost.token")).thenReturn(tokenCookie);

      InetSocketAddress address = mock(InetSocketAddress.class);
      InetAddress inetAddress = mock(InetAddress.class);
      when(address.getAddress()).thenReturn(inetAddress);
      when(request.getClientAddress()).thenReturn(address);

      Response response = mock(Response.class);
      ByteArrayOutputStream output = new ByteArrayOutputStream();
      when(response.getOutputStream()).thenReturn(output);

      container.handle(request, response);
      return new CapturedResponse(new String(output.toByteArray(), StandardCharsets.UTF_8));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }
}
