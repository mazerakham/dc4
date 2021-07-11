package dc4.model;

import java.time.Instant;

public abstract class AbstractModel {

  public Long id;

  public Instant timestamp = Instant.now();

}
