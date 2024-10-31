package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

public class CatCommand {
  public String execute(Command command, InterpreterContext context) {
    if (command.getArguments().isEmpty() || command.getOutputFile() != null) {
      String output = "";
      if (!command.getArguments().isEmpty() && command.getOutputFile() != null) {
        if (command.getPreviousCommand() != null) {
          if (command.getPreviousCommand().getName().equals("help"))
            output = new HelpCommand().execute();
          else {
            for (String arg : command.getArguments()) {
              output += arg + '\n';
            }
            output = output.substring(0, output.length() - 1);
          }
        } else
          output = catWithFile(command.getArguments().get(0), context);
      } else {
        System.out.println("You can exit by write '-1' (note if you write -1 at any line, the line will be ignored)");
        Scanner scanner = new Scanner(System.in);
        if (command.getOutputFile() != null) {
          output = catWithOperators(scanner);
        } else {
          catAlone(scanner);
        }
      }
      return output;
    }
    return catWithFile(command.getArguments().get(0), context);
  }

  public void catAlone(Scanner scanner) {
    do {
      String line = scanner.nextLine();
      if (line.contains("-1")) {
        break;
      }
      System.out.println(line);
    } while (true);
  }

  public String catWithFile(String filePath, InterpreterContext context) {
    File file;
    StringBuilder content = new StringBuilder();
    if (new File(filePath).isAbsolute()) {
      file = new File(filePath);
    } else {
      file = new File(context.getCurrentDirectory(), filePath);
    }
    // Resolve path to handle ".." or ".", get the absolute path
    try {
      file = file.getCanonicalFile(); // Resolves ".." and other relative paths
    } catch (IOException e) {
      return "Error: Unable to resolve path.";
    }
    // Check if the path exists and is a file and has access
    if (!file.exists()) {
      return "Error: No such file: " + file.getPath();
    }
    if (!file.isFile()) {
      return "Error: Not a file: " + file.getPath();
    }
    try (Scanner scanner = new Scanner(file)) { // Try-with-resources for safety
      while (scanner.hasNextLine()) {
        content.append(scanner.nextLine()).append('\n');
      }
      scanner.close();
      if (content.length() > 0) {
        content.deleteCharAt(content.length() - 1);
      }
      return content.toString();
    } catch (IOException e) {
      System.out.println("An error occurred while reading the file: " + e.getMessage());
      return "Error: Unable to read file.";
    }
  }

  public String catWithOperators(Scanner scanner) {
    StringBuilder output = new StringBuilder();
    do {
      String line = scanner.nextLine();
      if (line.contains("-1")) {
        break;
      }
      output.append(line).append('\n');
    } while (true);
    if (output.length() > 0) {
      output.deleteCharAt(output.length() - 1);
    }
    return output.toString();
  }
}
