package dc4.websockets;

import static com.google.common.base.Preconditions.checkState;

import java.util.List;

import com.google.common.collect.Lists;

import bowser.websocket.ClientSocket;
import bowser.websocket.WebSocketServer;
import ox.Json;
import ox.Log;
import ox.Threads;

public class DC4WebsocketsServer {

  private final WebSocketServer server;

  private final List<WebsocketsHandler> handlers;

  public DC4WebsocketsServer(int port) {
    this.server = new WebSocketServer(port);
    this.handlers = Lists.newArrayList();
  }

  public DC4WebsocketsServer handler(WebsocketsHandler handler) {
    handlers.add(handler);
    return this;
  }

  public DC4WebsocketsServer start() {
    server.onOpen(this::listenToSocket).start();
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
    WebsocketMessage message = parseWebSocketMessage(s).withSocket(socket);
    Log.info("Processing websocket message: " + message);
    for (WebsocketsHandler handler : handlers) {
      if (message.channel.equals(handler.channel)) {
        handler.handle(message);
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

  private WebsocketMessage parseWebSocketMessage(String message) {
    Json json = new Json(message);
    return new WebsocketMessage(json.get("channel"), json.get("command"), json.getJson("data"));
  }

}