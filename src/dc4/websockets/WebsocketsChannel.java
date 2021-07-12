package dc4.websockets;

import static ox.util.Utils.checkNotEmpty;
import static ox.util.Utils.normalize;

import java.util.function.Consumer;

import ox.x.XMap;

public abstract class WebsocketsChannel extends WebsocketsHandler {

  private final String channel;
  
  private final XMap<String, Consumer<WebsocketsMessage>> commands;
  
  public WebsocketsChannel(String channel) {
    this.channel = checkNotEmpty(normalize(channel));
    this.commands = XMap.create();
  }
  
  protected void command(String command, Consumer<WebsocketsMessage> commandHandler) {
    this.commands.put(command, commandHandler);
  }

  @Override
  public boolean handle(WebsocketsMessage message) {
    if (channel.equals(message.channel) && commands.containsKey(message.command)) {
      commands.get(message.command).accept(message);
      return true;
    } else {
      return false;
    }
  }
}
