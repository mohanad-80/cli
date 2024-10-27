package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.io.IOException;

public class CdCommand {
  public String execute(Command command, InterpreterContext context) {
    // Check if argument is empty and show usage message
    if (command.getArguments().isEmpty()) {
      return "Error: Usage: cd <directory>";
    }

    // Get the path from command arguments
    String targetPath = command.getArguments().get(0);
    File dir;

    // Check if path is absolute or relative
    if (new File(targetPath).isAbsolute()) {
      dir = new File(targetPath);
    } else {
      // Create File object using the current directory as base for relative paths
      dir = new File(context.getCurrentDirectory(), targetPath);
    }

    // Resolve path to handle ".." or ".", get the absolute path
    try {
      dir = dir.getCanonicalFile(); // Resolves ".." and other relative paths
    } catch (IOException e) {
      return "Error: Unable to resolve path.";
    }

    // Check if the path exists and is a directory and has access
    if (!dir.exists()) {
      return "Error: No such directory: " + dir.getPath();
    }
    if (!dir.isDirectory()) {
      return "Error: Not a directory: " + dir.getPath();
    }
    if (!dir.canRead()) {
      return "Error: Permission denied: " + dir.getPath();
    }

    // Update context with the new absolute path
    context.setCurrentDirectory(dir.getAbsolutePath());
    return "Changed directory to: " + dir.getAbsolutePath();
  }
}