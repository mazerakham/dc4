package dc4.cicd;

import jarjar.JarJar;
import ox.File;

public class Notebook1 {

  public void run() {
    JarJar.project(File.home("workspace/dc4"))
        .main("dc4.DC4Server")
        .skipCompile()
        .clean(false)
        .verbose()
        .build(File.downloads("DC4Server.jar"));

    SSHConnection connection = new SSHConnection("jakemirra.com");
    
    connection.shell().run("");
  }

  public static void main(String... args) {
    new Notebook1().run();
  }
}
