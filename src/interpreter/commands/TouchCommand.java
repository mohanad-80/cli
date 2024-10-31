package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

public class TouchCommand {
    public String execute(Command command, InterpreterContext context) {
        // Check if there are no arguments and show usage message
        if (command.getArguments().isEmpty()) {
            return "Error: Usage: touch <directory1> <directory2> ...";
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

            // Check if the directory already exists
            if (dir.exists()) {
                output.append("File already exists: ").append(dirName).append("\n");
            }
            else {
                try {
                    boolean created = dir.createNewFile();
                    if (created) {
                        output.append("File created: ").append(dirName).append("\n");
                    }
                    else {
                        output.append("Failed to create file: ").append(dirName).append("\n");
                    }
                }
                catch (IOException e) {
                    output.append("Failed to create file ").append(dirName)
                            .append(": ").append(e.getMessage()).append("\n");
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
