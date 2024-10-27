package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;

import java.io.File;
import java.util.List;

public class LsCommand {
  public String execute(Command command, InterpreterContext context) {
    boolean showAll = false;
    boolean reverseOrder = false;
    File currentDir = new File(context.getCurrentDirectory());
    StringBuilder output = new StringBuilder();
    List<String> arguments = command.getArguments();

    // check the flags and directory path in the arguments
    for (String arg : arguments) {
      if (arg.equals("-a")) {
        showAll = true;
      } else if (arg.equals("-r")) {
        reverseOrder = true;
      } else {
        currentDir = new File(arg); // Assume it's a directory path if it's not a flag
      }
    }

    // Check if the path exists and is a directory
    if (!currentDir.exists()) {
      return "Error: Directory does not exist: " + currentDir.getPath();
    }
    if (!currentDir.isDirectory()) {
      return "Error: Not a directory: " + currentDir.getPath();
    }

    String[] files = currentDir.list();

    if (files == null) {
      return "Error: Unable to access directory contents.";
    }
    if (files.length == 0) {
      return "Directory is empty.";
    }

    // if -r flag is found loop in reverse
    if (reverseOrder) {
      for (int i = files.length - 1; i >= 0; i--) {
        // skip hidden files if -a is not found
        if (!showAll && files[i].startsWith(".")) {
          continue;
        }
        output.append(files[i]).append("\n");
      }
    } else {
      for (String file : files) {
        // skip hidden files
        if (!showAll && file.startsWith(".")) {
          continue;
        }
        output.append(file).append("\n");
      }
    }
    return output.toString();
  }
}
