package dc4;

import java.util.UUID;

import org.simpleframework.http.Cookie;

import bowser.model.Request;
import bowser.model.RequestHandler;
import bowser.model.Response;
import dc4.db.UserDB;
import dc4.model.User;
import ox.Pair;
import ox.x.XOptional;

public class Authenticator implements RequestHandler {

  private final UserDB userDB = new UserDB();

  @Override
  public boolean process(Request request, Response response) {
    XOptional<User> user = XOptional.empty();
    
    String tokenString = request.cookie("token");
    if (tokenString != null) {
      user = userDB.getByToken(UUID.fromString(tokenString));
      if (user.isPresent()) {
        request.put("user", user);
        return false;
      }
    }
    
    Pair<User, Cookie> userCookie = makeUserAndCookie();
    response.cookie(userCookie.b);
    request.put("user", userCookie.a);
    return false;
  }

  private Pair<User, Cookie> makeUserAndCookie() {
    User user = userDB.insert(new User().token(UUID.randomUUID()));
    return Pair.of(user, new Cookie("token", user.token));
  }

}
