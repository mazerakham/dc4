package dc4.websockets;

public abstract class WebsocketsHandler {

  public final String channel;

  public WebsocketsHandler(String channel) {
    this.channel = channel;
  }

  public abstract void handle(WebsocketMessage message);

}
