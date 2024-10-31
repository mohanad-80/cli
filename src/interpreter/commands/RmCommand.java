package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.util.List;

public class RmCommand {
  public String execute(Command command, InterpreterContext context) {
    List<String> arguments = command.getArguments();

    if (arguments.isEmpty()) {
      return "Error: rm requires at least one argument";
    }

    StringBuilder output = new StringBuilder();
    for (String arg : arguments) {
      File target = relativeOrAbsolutePath(arg, context);

      if (!target.exists()) {
        output.append("Error: ").append(arg).append(" does not exist.\n");
        continue;
      }

      if (target.isDirectory()) {
        output.append("Error: ").append(arg).append(" is a directory.\n");
        continue;
      }

      output.append(deleteFile(target)).append("\n");
    }
    return output.toString();
  }

  private String deleteFile(File file) {
    if (!file.exists()) {
      return "Error: " + file.getPath() + " does not exist";
    }

    if (file.isDirectory()) {
      return "Error: Could not remove directory: " + file.getPath();
    } else if (file.isFile()) {
      return file.delete() ? "Removed file: " + file.getPath() : "Error: Could not remove file: " + file.getPath();
    }

    return "Error: " + file.getPath() + " is not removable.";
  }

  private File relativeOrAbsolutePath(String path, InterpreterContext context) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(context.getCurrentDirectory(), path);
    }
    return file;
  }

}
