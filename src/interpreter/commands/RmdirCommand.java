package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class RmdirCommand {
    public String execute(Command command, InterpreterContext context) {
        // Check if there are no arguments and show usage message
        if (command.getArguments().isEmpty()) {
            return "Error: Usage: rmdir <directory1> <directory2> ...";
        }

        // Get the current directory from context
        File baseDir = new File(context.getCurrentDirectory());
        List<String> arguments = command.getArguments();
        StringBuilder output = new StringBuilder();

        for (String dirName : arguments) {

            dirName = dirName.replace("\"", "");

            File dir = new File(baseDir, dirName);

            // Check if the directory name is invalid
            String invalidChars = "<>?/:*"; // Invalid characters
            boolean isValid = true;
            for (char c : invalidChars.toCharArray()) {
                if (dirName.indexOf(c) >= 0) {
                    output.append("Invalid directory name: ").append(dirName).append("\n");
                    isValid = false;
                    break;
                }
            }

            if (!isValid) continue;

            // Check if the directory does not exist
            if (!dir.exists()) {
                output.append("Directory does not exist: ").append(dirName).append("\n");
            } else {
                try {
                    Files.delete(dir.toPath());
                    output.append("Directory removed: ").append(dirName).append("\n");
                } catch (IOException e) {
                    output.append("Failed to remove directory ").append(dirName)
                          .append(": ").append(e.getMessage()).append(" is not empty\n");
                }
            }
        }

        // Remove the last newline to avoid extra spacing
        if (!output.isEmpty() && output.charAt(output.length() - 1) == '\n') {
            output.setLength(output.length() - 1);
        }
        return output.toString();
    }
}