package dc4.db;

import java.time.Instant;
import java.util.UUID;

import dc4.model.Session;
import ez.Table;
import ox.x.XOptional;

public class SessionDB extends DC4DB<Session> {

  @Override
  protected Table getTable() {
    return new Table("session")
        .idColumn()
        .column("timestamp", Instant.class)
        .column("userId", Long.class)
        .column("token", UUID.class).index()
        .column("expiration", Instant.class);
  }

  public XOptional<Session> getByToken(UUID token) {
    return getIndexed("token", token).only();
  }
}
