package dc4;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import bowser.model.Request;
import bowser.model.RequestHandler;
import bowser.model.Response;
import dc4.db.SessionDB;
import dc4.db.UserDB;
import dc4.model.Session;
import dc4.model.User;
import ox.x.XOptional;

public class Authenticator implements RequestHandler {

  private final SessionDB sessionDB = new SessionDB();
  private final UserDB userDB = new UserDB();

  @Override
  public boolean process(Request request, Response response) {
    User user;
    Session session;    
    XOptional<Session> validSessionMaybe = getAndValidateSession(CookieManager.getHostCookie(request, "token"));

    if (validSessionMaybe.isPresent()) {
      session = validSessionMaybe.get();
      sessionDB.update(session.id, "expiration", session.expiration = Instant.now().plus(Duration.ofDays(30)));
      user = userDB.get(session.userId);
    } else {
      user = userDB.insert(new User());
      session = sessionDB.insert(new Session(user.id, UUID.randomUUID(), Instant.now().plus(Duration.ofDays(30))));
    }
    
    long days = Duration.between(Instant.now(), session.expiration).toDays();
    CookieManager.setHostCookie(response, "token", session.token.toString(), (int) days, TimeUnit.DAYS);
    request.put("user", user);
    return false;
  }

  private XOptional<Session> getAndValidateSession(String tokenString) {
    if (tokenString == null || tokenString.isEmpty()) {
      return XOptional.empty();
    }

    try {
      UUID.fromString(tokenString);
    } catch (Exception e) {
      return XOptional.empty();
    }

    UUID token = UUID.fromString(tokenString);
    XOptional<Session> session = sessionDB.getByToken(token);
    return session.compute(s -> s.isExpired() ? XOptional.empty() : session, XOptional.empty());
  }


}
