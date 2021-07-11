package dc4;

import bowser.WebServer;
import dc4.api.DC4APIController;

public class DC4APIServer {
  
  public DC4APIServer start() {
    WebServer apiServer = new WebServer("DC4 API Server", DC4Server.API_PORT, false)
        .add(new Authenticator())
        .controller(new DC4APIController())
        .start();
    
    return this;
  }
}
