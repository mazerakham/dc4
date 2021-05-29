package dc4.model;

import java.time.Instant;
import java.util.UUID;

public class Session extends AbstractModel {

  public final long userId;

  public final UUID token;

  public Instant expiration;

  public Session(long userId, UUID token, Instant expiration) {
    this.userId = userId;
    this.token = token;
    this.expiration = expiration;
  }

  public boolean isExpired() {
    return Instant.now().isAfter(expiration);
  }

}
