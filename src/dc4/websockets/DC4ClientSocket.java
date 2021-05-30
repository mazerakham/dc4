package dc4.websockets;

import bowser.websocket.ClientSocket;

public class DC4ClientSocket {

  public ClientSocket socket;

  public DC4ClientSocket(ClientSocket socket) {
    this.socket = socket;
  }

  public DC4ClientSocket send(WebsocketMessage message) {
    socket.send(message.toJson());
    return this;
  }
}