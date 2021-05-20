package dc4;

import bowser.WebServer;
import fabel.JSXHandler;
import ox.Config;
import ox.Log;

public class DC4Server {

  private static final Config config = Config.load("dc4");
  private static final int WEBSERVER_PORT = config.getInt("webserverPort", 8080);
  private static final int API_PORT = config.getInt("apiPort", 7070);

  public void start() {


    WebServer server = new WebServer("DC4 Server", WEBSERVER_PORT, false)
        .controller(new DC4Controller());
    server.add(new JSXHandler(server));
    server.start();
    Log.debug("Server started on port " + WEBSERVER_PORT + ".");


    WebServer apiServer = new WebServer("DC4 API Server", API_PORT, false)
        .controller(new DC4APIController())
        .start();
    Log.debug("API Server started on port " + API_PORT + ".");

  }

  public static void main(String... args) {
    new DC4Server().start();
  }
}
