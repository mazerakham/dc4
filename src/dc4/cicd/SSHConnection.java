package dc4.cicd;

import static ox.util.Utils.normalize;
import static ox.util.Utils.propagate;
import static ox.util.Utils.sleep;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;

import ox.File;
import ox.Log;
import ox.util.SplitOutputStream;

public class SSHConnection {

  public final String host;
  public final String user = "root";
  private Session session;

  public SSHConnection(String host) {
    this.host = host;

    connectSession();
  }

  public SSHShell shell() {
    try {
      return new SSHShell(session);
    } catch (Exception e) {
      Log.error("Failed to create shell, attempting to create a new session...");

      try {
        session.disconnect();
      } catch (Exception ee) {
        ee.printStackTrace();
      }

      connectSession();
      return new SSHShell(session);
    }
  }

  private void connectSession() {
    try {
      session = jsch.getSession(user, host);
      session.connect(3000);
    } catch (Exception e) {
      throw propagate(e);

    }

    Log.debug("Connected to " + user + "@" + host);
  }

  public byte[] run(String command) {
    return run(command, null, true);
  }

  public byte[] run(String command, Integer timeoutSeconds, boolean verbose) {
    Log.debug("SSHConnection: Running command: " + command);
    try {
      ChannelExec channel = (ChannelExec) session.openChannel("exec");
      channel.setCommand(command);
      InputStream in = channel.getInputStream();

      ByteArrayOutputStream storedOutput = new ByteArrayOutputStream();
      OutputStream out = storedOutput;
      if (verbose) {
        out = new SplitOutputStream(System.out, storedOutput) {
          @Override
          public void close() throws IOException {
            // do nothing, we don't want to close System.out, then all the logging stops :(
          }
        };
      }
      channel.setOutputStream(out);
      channel.setErrStream(out);

      channel.connect(3000);

      await(channel, in, timeoutSeconds);

      int status = channel.getExitStatus();

      channel.disconnect();

      if (status != 0) {
        throw new SSHException("Exit status: " + status,
            new String(storedOutput.toByteArray(), StandardCharsets.UTF_8));
      }

      return storedOutput.toByteArray();
    } catch (Exception e) {
      throw propagate(e);
    }
  }

  public Optional<Exception> tryRun(String command) {
    try {
      run(command);
      return Optional.empty();
    } catch (Exception e) {
      return Optional.of(e);
    }
  }

  private String await(Channel channel, InputStream in, Integer timeoutSeconds) throws Exception {
    StringBuilder sb = new StringBuilder();
    byte[] tmp = new byte[1024];
    Instant startTime = Instant.now();
    while (true) {
      while (in.available() > 0) {
        int i = in.read(tmp, 0, 1024);
        if (i < 0) {
          break;
        }
        sb.append(new String(tmp, 0, i, StandardCharsets.UTF_8));
      }
      if (channel.isClosed()) {
        if (in.available() > 0) {
          continue;
        }
        return sb.toString();
      }
      if (timeoutSeconds != null) {
        if (ChronoUnit.SECONDS.between(startTime, Instant.now()) > timeoutSeconds) {
          throw new RuntimeException("Timed out!");
        }
      }
      sleep(1);
    }
  }

  public SSHConnection gitPull(String... repos) {
    SSHShell shell = shell();

    for (String repo : repos) {
      shell.run("git -C " + repo + " pull &");
    }

    shell.run("wait");
    shell.exit();

    return this;
  }

  public SSHConnection gitCheckout(String repo, String branch) {
    SSHShell shell = shell();

    shell.run("git -C " + repo + " fetch ");
    shell.run("git -C " + repo + " checkout " + branch);
    shell.run("git -C " + repo + " pull");

    shell.exit();

    return this;
  }

  public SSHConnection screenQuit(String screenName) {
    try {
      run("screen -S " + screenName + " -X quit");
    } catch (Exception e) {
      Log.warn("existing screen not found: " + screenName);
    }
    return this;
  }

  public SSHConnection screen(String screenName, String command) {
    run("screen -S " + screenName + " -d -m " + command);
    return this;
  }

  public SSHConnection wget(String url, String targetFileName) {
    run("wget --quiet --no-check-certificate -O " + targetFileName + " " + url);
    return this;
  }

  public void exit() {
    session.disconnect();
  }

  private static final JSch jsch;
  static {
    JSch.setConfig("StrictHostKeyChecking", "no");
    jsch = new JSch();
    File.home(".ssh").children().forEach(file -> {
      if (file.extension().equals("pem") || file.getName().equals("id_rsa")) {
        try {
          jsch.addIdentity(file.getPath());
        } catch (JSchException e) {
          throw propagate(e);
        }
      }
    });
  }

  public static class SSHException extends RuntimeException {

    public final String log;

    public SSHException(String message, String log) {
      super(message);
      this.log = normalize(log);
    }

  }

  public static void main(String[] args) {
    SSHConnection conn = new SSHConnection("staging.ender.com");
    SSHShell shell = conn.shell();
    Log.debug(shell.run("cd ~/.ender/log", "tail -1000 `ls -t | head -n1`"));
    shell.exit();
    conn.exit();
  }

}
