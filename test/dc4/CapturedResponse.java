package dc4;

import static ox.util.Utils.propagate;

import ox.Json;
import ox.Log;

public class CapturedResponse {
  public final String text;

  public CapturedResponse(String text) {
    this.text = text;
  }

  public Json json() {
    try {
      return new Json(text);
    } catch (Exception e) {
      Log.error("Invalid json: " + text);
      throw propagate(e);
    }
  }
}
