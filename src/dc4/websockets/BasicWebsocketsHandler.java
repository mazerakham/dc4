package dc4.websockets;

import ox.Json;
import ox.Log;

public class BasicWebsocketsHandler extends WebsocketsHandler {

  public BasicWebsocketsHandler(String channel) {
    super(channel);
  }

  @Override
  public void handle(WebsocketMessage message) {
    Log.debug("We are here in BasicWebsocketsHandler.");
    Log.debug(message.toJson());

    if (message.command.equals("pong")) {
      Log.debug("Ping pong was successful!");
      return;
    } else if (message.command.equals("hello")) {
      // Now we immediately send a message back to client as if we were doing a handshake.
      message.socket.send(new WebsocketMessage("basic", "ping",
          Json.object().with("msg", "Message from BasicWebsocketsHandler.java's handle method.")));
    } else {
      throw new RuntimeException(message.command);
    }

  }
}
