package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class MvCommand {
  public String execute(Command command, InterpreterContext context) {
    List<String> arguments = command.getArguments();

    if (arguments.size() < 2) {
      return "Error: mv requires two arguments at least";
    }

    if (arguments.size() == 2) {
      return twoArguments(arguments.get(0), arguments.get(1), context);
    } else {
      return moreThanTwoArguments(arguments, context);
    }
  }

  public String twoArguments(String sourcePath, String destPath, InterpreterContext context) {
    File sourceFile = relativeOrAbsolutePath(sourcePath, context);
    File destinationFile = relativeOrAbsolutePath(destPath, context);

    if (!sourceFile.exists()) {
      return "Error: Source file or directory does not exist.";
    }

    try {
      if (destinationFile.isDirectory()) {
        File targetFile = new File(destinationFile, sourceFile.getName());
        Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return "Moved \"" + sourceFile.getPath() + "\" to directory \"" + destinationFile.getPath() + "\".";
      } else if (destinationFile.isFile()) {
        return "Error: Unable to move/rename \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath()
            + "\".";
      } else {
        Files.move(sourceFile.toPath(), destinationFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        return "Renamed \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".";
      }
    } catch (IOException e) {
      return "Error: Unable to move/rename \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".";
    }
  }

  public String moreThanTwoArguments(List<String> arguments, InterpreterContext context) {
    StringBuilder output = new StringBuilder();
    String destPath = arguments.get(arguments.size() - 1);
    File destinationFile = relativeOrAbsolutePath(destPath, context);
    if (!destinationFile.isDirectory()) {
      return "Error: Destination is not a directory.";
    }
    for (int i = 0; i < arguments.size() - 1; i++) {
      File sourceFile = relativeOrAbsolutePath(arguments.get(i), context);

      if (!sourceFile.exists()) {
        output.append("Error: Source file \"").append(sourceFile.getPath()).append("\" does not exist.\n");
        continue;
      }
      File targetFile = new File(destinationFile, sourceFile.getName());
      try {
        Files.move(sourceFile.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
        output.append("Moved\"").append(sourceFile.getPath()).append("\" to \"").append(targetFile.getPath())
            .append("\".\n");
      } catch (IOException e) {
        output.append("Error: Unable to move \"").append(sourceFile.getPath()).append("\" to \"")
            .append(destinationFile.getPath()).append("\".\n");
      }
    }
    return output.toString();
  }

  private File relativeOrAbsolutePath(String path, InterpreterContext context) {
    File file = new File(path);
    if (!file.isAbsolute()) {
      file = new File(context.getCurrentDirectory(), path);
    }
    return file;
  }
}
