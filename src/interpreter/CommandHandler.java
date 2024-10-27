package interpreter;

import interpreter.commands.*;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;

public class CommandHandler {
  private final InterpreterContext context; // Use context for state management

  public CommandHandler() {
    this.context = new InterpreterContext();
  }

  public void execute(Command command) {
    while (command != null) {
      String output = null;

      // Handle each command by name
      switch (command.getName()) {
        case "cd":
          output = new CdCommand().execute(command, context);
          break;
        case "ls":
          output = new LsCommand().execute(command, context);
          break;
        case "exit":
          new ExitCommand().execute();
          return;
        case "help":
          break;
        default:
          System.out.println("Unknown command: " + command.getName());
      }

      // Handle redirection to a file
      if (command.getOutputFile() != null) {
        try (FileWriter writer = new FileWriter(command.getOutputFile(), command.isAppend())) {
          writer.write(output);
        } catch (IOException e) {
          System.out.println("Error writing to file: " + e.getMessage());
        }
      } else if (command.getNextCommand() != null) {
        // Pass output to next command in case of piping
        String[] outputArgs = output.trim().split("\\s+");
        command.getNextCommand().getArguments().addAll(Arrays.asList(outputArgs));
      } else {
        System.out.println(output);
      }

      // Move to next command in the pipeline (if any)
      command = command.getNextCommand();
    }
  }
}
