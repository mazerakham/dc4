package dc4;

import bowser.WebServer;
import dc4.db.DC4DB;
import dc4.db.upgrade.Backcompat;
import fabel.JSXHandler;
import ox.Config;
import ox.Log;

public class DC4Server {

  private static final Config config = Config.load("dc4");
  private static final int WEBSERVER_PORT = config.getInt("webserverPort", 8080);
  private static final int API_PORT = config.getInt("apiPort", 7070);
  private static final String SERVER_TYPE = config.get("serverType", "dev");
  public static final String API_URL = SERVER_TYPE.equals("dev") ? "http://localhost:7070"
      : "https://jakemirra.com/api";

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
  }

  public static void main(String... args) {
    Log.logToFolder("dc4");
    DC4DB.connectToDatabase();
    new Backcompat().run();
    new DC4Server().start();
  }
}
