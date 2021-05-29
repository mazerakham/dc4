package dc4.model;

public class User extends AbstractModel {

  @Override
  public String toString() {
    return String.format("[id=%d]", this.id);
  }
}
