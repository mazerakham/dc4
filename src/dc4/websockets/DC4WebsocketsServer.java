package dc4.websockets;

import static com.google.common.base.Preconditions.checkState;

import bowser.websocket.ClientSocket;
import bowser.websocket.WebSocketServer;
import dc4.DC4Server;
import ox.Json;
import ox.Log;
import ox.Threads;
import ox.x.XList;

/**
 * This class is really doing the job of two classes, and I hope to refactor eventually. 
 * 
 * 1.  It provides an extension of the functionality of WebSocketServer (Bowser) to support structured channels and commands.
 * 
 * 2.  It implements a websockets server for the DC4 project.
 */
public class DC4WebsocketsServer {

  private final WebSocketServer server;

  private final XList<WebsocketsHandler> handlers = XList.create();

  public DC4WebsocketsServer() {
    server = new WebSocketServer(DC4Server.WEBSOCKETS_PORT).onOpen(this::listenToSocket);
    handler(new BasicWebsocketsChannel()); 
  }

  public DC4WebsocketsServer handler(WebsocketsHandler handler) {
    handlers.add(handler.init());
    return this;
  }

  public DC4WebsocketsServer start() {
    server.start();
    return this;
  }

  private void listenToSocket(ClientSocket socket) {
    Log.info("Client connected: " + socket);
    socket.onMessage(s -> Threads.run(() -> delegateMessageToListeners(s, new DC4ClientSocket(socket))));
  }

  private void delegateMessageToListeners(String s, DC4ClientSocket socket) {
    if (!isValidMessage(s)) {
      Log.info("Received malformed websocket message: %s", s);
      return;
    }
    WebsocketsMessage message = parseWebSocketMessage(s).withSocket(socket);
    Log.info("Processing websocket message: " + message);
    for (WebsocketsHandler handler : handlers) {
      if (handler.handle(message)) {
        return;
      }
    }
  }

  private boolean isValidMessage(String s) {
    Json json;
    try {
      json = new Json(s);
      checkState(json.isObject());
    } catch (Exception e) {
      return false;
    }
    return (json.hasKey("channel") && json.hasKey("command") && json.hasKey("data"));
  }

  private WebsocketsMessage parseWebSocketMessage(String message) {
    Json json = new Json(message);
    return new WebsocketsMessage(json.get("channel"), json.get("command"), json.getJson("data"));
  }

}
