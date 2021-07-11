package dc4;

import java.time.Duration;
import java.util.UUID;

import dc4.db.SessionDB;
import dc4.db.UserDB;
import dc4.model.Session;
import dc4.model.User;
import ox.util.Time;

public class ModelWizard {

  private final SessionDB sessionDB = new SessionDB();
  private final UserDB userDB = new UserDB();
  
  
  public User createUser() {
    return userDB.insert(new User());
  }
  
  public Session createSession(User user) {
    return sessionDB.insert(new Session(user.id, UUID.randomUUID(), Time.nowInstant().plus(Duration.ofDays(30))));
  }
}
