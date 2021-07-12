package dc4.websockets;

import java.util.function.Consumer;

import ox.Json;
import ox.Log;

public class BasicWebsocketsChannel extends WebsocketsChannel {

  public BasicWebsocketsChannel() { 
    super("basic");
  }

  @Override
  public WebsocketsHandler init() {
    command("pong", pong);
    command("hello", hello);
    return this;
  }
  
  private final Consumer<WebsocketsMessage> pong = message -> {
    Log.debug("Ping pong was successful!");
  };
  
  private final Consumer<WebsocketsMessage> hello = message -> {
    message.socket.send(new WebsocketsMessage("basic", "ping", Json.object().with("msg", "Hey.")));
  };

}
