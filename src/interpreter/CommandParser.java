package interpreter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class CommandParser {
  public Command parse(String input) {
    if (input == null || input.isEmpty()) {
      return null;
    }
    // Split by pipe operator first for command chaining
    String[] pipeSegments = input.split("\\|");
    Command firstCommand = null;
    Command currentCommand = null;
    Command previousCommand = null;

    for (String segment : pipeSegments) {
      // Check for output redirection operators `>` and `>>`
      String[] redirectionSegments = { segment.trim() };
      boolean append = false;

      if (segment.contains(">>")) {
        redirectionSegments = segment.split(">>", 2);
        append = true;
      } else if (segment.contains(">")) {
        redirectionSegments = segment.split(">", 2);
      }

      String commandPart = redirectionSegments[0].trim();
      List<String> parts = new ArrayList<>(Arrays.asList(commandPart.split("\\s+")));
      String commandName = parts.get(0);
      List<String> arguments;

      // Safely handle arguments extraction
      if (parts.size() > 1) {
        arguments = parts.subList(1, parts.size());
      } else {
        arguments = new ArrayList<>();
      }

      currentCommand = new Command(commandName, arguments);

      // Handle redirection if present
      if (redirectionSegments.length > 1) {
        // Remove any leading `>` symbols
        String outputFile = redirectionSegments[1].replaceAll("^>+", "").trim();
        currentCommand.setOutputFile(outputFile, append);
      }

      // Link the previous command for piping
      if (previousCommand != null) {
        previousCommand.setNextCommand(currentCommand);
      }

      if (firstCommand == null) {
        firstCommand = currentCommand;
      }

      previousCommand = currentCommand;
    }

    // Return the first command in the pipeline
    return firstCommand;
  }
}
