package dc4.model;

import java.time.Instant;
import java.util.UUID;

public class User {

  public long id;

  public Instant timestamp;

  public UUID token;

  public User() {
    this.timestamp = Instant.now();
  }

  public User token(UUID token) {
    this.token = token;
    return this;
  }
}
