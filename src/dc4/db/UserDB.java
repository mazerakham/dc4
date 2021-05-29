package dc4.db;

import java.time.Instant;
import java.util.UUID;

import dc4.model.User;
import ez.Row;
import ez.Table;
import ox.x.XOptional;

public class UserDB extends DC4DB<User> {

  @Override
  protected Table getTable() {
    return new Table("user")
        .idColumn()
        .column("timestamp", Instant.class);
  }

  public XOptional<User> getByToken(UUID token) {
    Row row = rawQuery().selectSingleRow("SELECT * FROM `user` WHERE `token` = ?", token);
    return row == null ? XOptional.empty() : XOptional.of(getTable().fromRow(row, User.class));
  }



}
