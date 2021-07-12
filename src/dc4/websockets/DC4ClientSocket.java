package dc4.websockets;

import bowser.websocket.ClientSocket;
import dc4.model.User;

public class DC4ClientSocket {

  public ClientSocket socket;
  
  public User user;

  public DC4ClientSocket(ClientSocket socket, User user) {
    this.socket = socket;
    this.user = user;
  }

  public DC4ClientSocket send(WebsocketsMessage message) {
    socket.send(message.toJson());
    return this;
  }
}