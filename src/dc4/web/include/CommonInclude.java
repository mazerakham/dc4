package dc4.web.include;

import bowser.model.Controller;

public class CommonInclude extends Controller {

  @Override
  public void init() {
    mapFolders("css", "html", "js");
    
    mapFolder("mjs", "js");
    mapFolder("jsx", "js");
    mapFolder("scss", "css");
    mapFolder("svg", "img");
    mapFolder("png", "img");
    mapFolder("gif", "img");
    mapFolder("woff2", "font");
  }

}
