package dc4.websockets;

import ox.Json;

public class WebsocketMessage {

  public final String channel;
  public final String command;
  public final Json data;
  public DC4ClientSocket socket = null;

  public WebsocketMessage(String channel, String command, Json data) {
    this.channel = channel;
    this.command = command;
    this.data = data;
  }

  public WebsocketMessage withSocket(DC4ClientSocket socket) {
    this.socket = socket;
    return this;
  }

  public Json toJson() {
    return Json.object()
        .with("channel", channel)
        .with("command", command)
        .with("data", data);
  }
}
