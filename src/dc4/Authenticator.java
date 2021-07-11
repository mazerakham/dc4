package dc4;

import java.time.Duration;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import bowser.model.Request;
import bowser.model.RequestHandler;
import bowser.model.Response;
import dc4.db.SessionDB;
import dc4.db.UserDB;
import dc4.model.Session;
import dc4.model.User;
import ox.Log;
import ox.util.Time;
import ox.x.XOptional;

public class Authenticator implements RequestHandler {

  private static final Duration COOKIE_DURATION = Duration.ofDays(30);
  
  private final SessionDB sessionDB = new SessionDB();
  private final UserDB userDB = new UserDB();
  

  @Override
  public boolean process(Request request, Response response) {
    User user;
    Session session;
    
    XOptional<Session> validSessionMaybe = getAndValidateSession(CookieManager.getHostCookie(request, "token"));
    Log.debug(validSessionMaybe);
    
    if (validSessionMaybe.isPresent()) {
      session = validSessionMaybe.get();
      sessionDB.update(session.id, "expiration", session.expiration = Time.nowInstant().plus(COOKIE_DURATION));
      user = userDB.get(session.userId);
    } else {
      user = userDB.insert(new User());
      session = sessionDB.insert(new Session(user.id, UUID.randomUUID(), Time.nowInstant().plus(COOKIE_DURATION)));
    }
    
    CookieManager.setHostCookie(response, "token", session.token.toString(), (int) COOKIE_DURATION.toDays(), TimeUnit.DAYS);
    request.put("user", user);
    return false;
  }

  private XOptional<Session> getAndValidateSession(String tokenString) {
    if (tokenString == null || tokenString.isEmpty()) {
      return XOptional.empty();
    }

    UUID token;
    try {
      token = UUID.fromString(tokenString);
    } catch (Exception e) {
      return XOptional.empty();
    }

    XOptional<Session> session = sessionDB.getByToken(token);
    return session.compute(s -> s.isExpired() ? XOptional.empty() : session, XOptional.empty());
  }


}
