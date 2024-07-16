package cn.nukkit.console;

import cn.nukkit.Server;
import org.jline.reader.Candidate;
import org.jline.reader.Completer;
import org.jline.reader.LineReader;
import org.jline.reader.ParsedLine;

import java.util.List;
import java.util.TreeSet;
import java.util.function.Consumer;

public record NukkitConsoleAccepter(Server server) implements Completer {

  @Override
  public void complete(LineReader lineReader, ParsedLine parsedLine, List<Candidate> candidates) {
    if (parsedLine.wordIndex() == 0) {
      if (parsedLine.word().isEmpty()) {
        this.addCandidates(s -> candidates.add(new Candidate(s)));
        return;
      }
      var names = new TreeSet<String>();
      this.addCandidates(names::add);
      for (String match : names) {
        if (!(match.toLowerCase().startsWith(parsedLine.word())))
          continue;
        candidates.add(new Candidate(match));
      }
    } else if (parsedLine.wordIndex() > 0 && !parsedLine.word().isEmpty()) {
      var word = parsedLine.word();
      var names = new TreeSet<String>();
      this.server().getOnlinePlayers().values().forEach((p) -> names.add(p.getName()));
      for (var match : names) {
        if (!(match.toLowerCase().startsWith(word.toLowerCase())))
          continue;
        candidates.add(new Candidate(match));
      }
    }
  }

  private void addCandidates(Consumer<String> commandConsumer) {
    for (var command : this.server().getCommandMap().getCommands().keySet())
      if (!command.contains(":"))
        commandConsumer.accept(command);
  }
}
