package dc4;

import bowser.WebServer;
import dc4.db.DC4DB;
import dc4.db.upgrade.Backcompat;
import dc4.web.DC4Controller;
import dc4.websockets.BasicWebsocketsHandler;
import dc4.websockets.DC4WebsocketsServer;
import fabel.JSXHandler;
import ox.Config;
import ox.Log;

public class DC4Server {

  public static final Config config = Config.load("dc4");
  public static final String SERVER_TYPE = config.get("serverType", "DEV");
  public static final int WEBSERVER_PORT = config.getInt("webserverPort", 8080);
  public static final int API_PORT = config.getInt("apiPort", 7070);
  public static final String API_URL = config.get("apiUrl", "http://localhost:" + API_PORT);
  public static final int WEBSOCKETS_PORT = config.getInt("websocketsPort", 42069 /* nice */);
  public static final String WEBSOCKETS_URL = config.get("websocketsUrl", "ws://localhost:" + WEBSOCKETS_PORT);

  public void start() {
    WebServer server = new WebServer("DC4 Server", WEBSERVER_PORT, false)
        .add(new Authenticator())
        .controller(new DC4Controller());
    server.add(new JSXHandler(server));
    server.start();
    Log.debug("Server started on port " + WEBSERVER_PORT + ".");

    WebServer apiServer = new WebServer("DC4 API Server", API_PORT, false)
        .add(new Authenticator())
        .controller(new DC4APIController())
        .start();
    Log.debug("API Server started on port " + API_PORT + ".");

    DC4WebsocketsServer websockets = new DC4WebsocketsServer(WEBSOCKETS_PORT)
        .handler(new BasicWebsocketsHandler("basic"))
        .start();
    Log.debug("Websockets Server started on port " + WEBSOCKETS_PORT + ".");
  }

  public static void main(String... args) {
    Log.logToFolder("dc4");
    DC4DB.connectToDatabase();
    new Backcompat().run();
    new DC4Server().start();
  }
}
