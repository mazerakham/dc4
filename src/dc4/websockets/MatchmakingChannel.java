package dc4.websockets;

import java.util.function.Consumer;

import dc4.model.User;
import dc4.service.MatchmakingService;

public class MatchmakingChannel extends WebsocketsChannel{

  private final MatchmakingService matchmakingService = new MatchmakingService();
  
  public MatchmakingChannel() {
    super("matchmaking");
  }
  
  @Override
  public WebsocketsHandler init() {
    command("enqueue", enqueue);
    command("dequeue", dequeue);
    return this;
  }
  
  private Consumer<WebsocketsMessage> enqueue = message -> {
    User user = message.getUser();
    matchmakingService.enqueue(user, message.socket);
  };
  
  private Consumer<WebsocketsMessage> dequeue = message -> {
    
  };
}
  


