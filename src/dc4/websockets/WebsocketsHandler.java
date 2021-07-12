package dc4.websockets;

public abstract class WebsocketsHandler {

  public abstract WebsocketsHandler init();
  
  public abstract boolean handle(WebsocketsMessage message);

}
