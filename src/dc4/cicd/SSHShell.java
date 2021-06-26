package dc4.cicd;

import static ox.util.Utils.propagate;
import static ox.util.Utils.sleep;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.Session;

import ox.Log;

public class SSHShell {

  private final Channel channel;
  private final PrintStream out;
  private final ByteArrayOutputStream buffer;

  public SSHShell(Session session) {
    try {
      channel = session.openChannel("shell");

      buffer = new ByteArrayOutputStream();
      channel.setOutputStream(buffer);

      out = new PrintStream(channel.getOutputStream());

      channel.connect(5000);

      awaitShell();
      buffer.reset();
    } catch (Exception e) {
      throw propagate(e);
    }
  }

  public String run(String... commands) {
    for (int i = 0; i < commands.length - 1; i++) {
      run(commands[i]);
    }
    return run(commands[commands.length - 1]);
  }

  public String run(String command) {
    Log.debug("SSHSHell: Running command: " + command);

    out.println(command);
    out.flush();
    awaitShell();
    String ret = stripeOutput(new String(buffer.toByteArray(), StandardCharsets.UTF_8));
    buffer.reset();
    return ret;
  }

  private String stripeOutput(String s) {
    // removing the first line, which is the command itself
    int i = s.indexOf('\n');
    s = s.substring(i + 1);

    // removing the last line, which is the prompt
    i = s.lastIndexOf('\n');
    if (i == -1 && s.indexOf('$') != -1) {
      return "";
    }
    return s.substring(0, i);
  }

  private void awaitShell() {
    for (int attempt = 0; attempt < 1000; attempt++) {
      String s = new String(buffer.toByteArray(), StandardCharsets.UTF_8);
      int i = s.indexOf('\n');
      if (i == -1) {
        sleep(100);
        continue;
      }
      i = s.indexOf('$', i);
      if (i == -1) {
        sleep(100);
        continue;
      } else {
        return;
      }
    }
    throw new RuntimeException("Never found shell prompt!");
  }

  public void exit() {
    channel.disconnect();
  }

}
