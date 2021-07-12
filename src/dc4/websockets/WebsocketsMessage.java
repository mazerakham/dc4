package dc4.websockets;

import dc4.model.User;
import ox.Json;

public class WebsocketsMessage {

  public final String channel;
  public final String command;
  public final Json data;
  public DC4ClientSocket socket = null;

  public WebsocketsMessage(String channel, String command, Json data) {
    this.channel = channel;
    this.command = command;
    this.data = data;
  }

  public WebsocketsMessage withSocket(DC4ClientSocket socket) {
    this.socket = socket;
    return this;
  }
  
  public User getUser() {
    return this.socket.user;
  }

  public Json toJson() {
    return Json.object()
        .with("channel", channel)
        .with("command", command)
        .with("data", data);
  }
  
  @Override
  public String toString() {
    return toJson().toString();
  }
}
