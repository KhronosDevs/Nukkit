package cn.nukkit.console;

import cn.nukkit.Server;
import cn.nukkit.event.server.ServerCommandEvent;
import lombok.Getter;
import lombok.SneakyThrows;
import net.minecrell.terminalconsole.SimpleTerminalConsole;
import org.jline.reader.LineReader;
import org.jline.reader.LineReaderBuilder;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class NukkitConsole extends SimpleTerminalConsole {

  private final Server server;

  private final BlockingQueue<String> consoleQueue;

  private final AtomicBoolean executingCommands;

  public NukkitConsole(Server server) {
    this.server = server;

    this.consoleQueue = new LinkedBlockingQueue<>();
    this.executingCommands = new AtomicBoolean(false);
  }

  @Override
  protected boolean isRunning() {
    return this.getServer().isRunning();
  }

  @Override
  protected void runCommand(String command) {
    if (!(this.getExecutingCommands().get())) {
      this.getConsoleQueue().add(command);
      return;
    }

    ServerCommandEvent event = new ServerCommandEvent(this.getServer().getConsoleSender(), command);
    this.getServer().getPluginManager().callEvent(event);

    if (!(event.isCancelled()))
      this.getServer().getScheduler().scheduleTask(
          () -> this.getServer().dispatchCommand(event.getSender(), event.getCommand()));
  }

  @SneakyThrows
  public String readConsoleLine() {
    return this.getConsoleQueue().take();
  }

  @Override
  protected LineReader buildReader(LineReaderBuilder builder) {
    builder.completer(new NukkitConsoleAccepter(this.getServer()));
    builder.appName("Nukkit");
    builder.option(LineReader.Option.HISTORY_BEEP, false);
    builder.option(LineReader.Option.HISTORY_IGNORE_DUPS, true);
    builder.option(LineReader.Option.HISTORY_IGNORE_SPACE, true);
    return super.buildReader(builder);
  }

  @Override
  protected void shutdown() {
    this.getServer().shutdown();
  }

  public void setExecutingCommands(boolean executingCommands) {
    if (this.getExecutingCommands().compareAndSet(!executingCommands, executingCommands) &&
        executingCommands)
      this.getConsoleQueue().clear();
  }
}
