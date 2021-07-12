package dc4.service;

import static com.google.common.base.Preconditions.checkState;

import java.util.ArrayDeque;

import dc4.model.User;
import dc4.websockets.DC4ClientSocket;
import dc4.websockets.WebsocketsMessage;
import ox.Json;
import ox.x.XMap;

public class MatchmakingService {

  ArrayDeque<Long> userQueue = new ArrayDeque<Long>();
  XMap<Long, DC4ClientSocket> userToSocket = XMap.create();
  XMap<Long, User> idUsers = XMap.create();
  
  public void enqueue(User user, DC4ClientSocket socket) {
    synchronized(userQueue) {      
      checkState(!userQueue.contains(user.id));
      userToSocket.put(user.id, socket);
      idUsers.put(user.id, user);
      userQueue.add(user.id);
      if (userToSocket.size() >= 2) {
        makeMatch(); 
      }
    }
  }
  
  public void dequeue(User user, DC4ClientSocket socket) {
    synchronized(userQueue) {      
      checkState(userQueue.contains(user.id));
      userToSocket.remove(user.id);
      idUsers.remove(user.id);
      userQueue.remove(user.id);
    }
  }
  
  public void makeMatch() {
    long userId1 = userQueue.removeFirst();
    long userId2 = userQueue.removeFirst();
    
    DC4ClientSocket socket1 = userToSocket.get(userId1);
    DC4ClientSocket socket2 = userToSocket.get(userId2);
    
    userToSocket.remove(userId1);
    userToSocket.remove(userId2);
    idUsers.remove(userId1);
    idUsers.remove(userId2);
    
    boolean coinflip = Math.random() > 0.5;
    String position1 = coinflip ? "1" : "2";
    String position2 = coinflip ? "2" : "1";
    socket1.send(new WebsocketsMessage("matchmaking", "matchFound", Json.object().with("position", position1)));
    socket2.send(new WebsocketsMessage("matchmaking", "matchFound", Json.object().with("position", position2)));
  }
}
