package dc4.web;

import bowser.WebServer;
import dc4.Authenticator;
import dc4.DC4Server;
import fabel.JSXHandler;

public class DC4Webserver {

  private WebServer webserver;

  public DC4Webserver() {
    webserver = new WebServer("DC4 Server", DC4Server.WEBSERVER_PORT, false)
        .add(new Authenticator())
        .controller(new DC4Controller());
    webserver.add(new JSXHandler(webserver));
  }
  
  public DC4Webserver start() {
    webserver.start();
    return this;
  }

}
