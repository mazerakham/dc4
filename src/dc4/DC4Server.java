package dc4;

import dc4.db.DC4DB;
import dc4.db.upgrade.Backcompat;
import dc4.web.DC4Webserver;
import dc4.websockets.DC4WebsocketsServer;
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
  
  private DC4Webserver webserver;
  private DC4APIServer apiServer;
  private DC4WebsocketsServer websockets;

  public DC4Server start() {
    webserver = new DC4Webserver().start();
    Log.debug("Server started on port " + WEBSERVER_PORT + ".");

    apiServer = new DC4APIServer().start();
    Log.debug("API Server started on port " + API_PORT + ".");

    websockets = new DC4WebsocketsServer().start();
    Log.debug("Websockets Server started on port " + WEBSOCKETS_PORT + ".");
    
    return this;
  }

  public static void main(String... args) {
    Log.logToFolder("dc4");
    DC4DB.connectToDatabase();
    new Backcompat().run();
    new DC4Server().start();
  }
}
