package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Scanner;

public class MvCommand {
    private boolean interactive = false;
    private boolean force = false;
    private boolean update = false;

    public String execute(Command command, InterpreterContext context) {
        List<String> arguments = command.getArguments();
        
        if (arguments.size() < 2) {
            if(arguments.size() == 1 && arguments.get(0).equals("--help")){
                String message = """
                Options:
                -f,--force : do not prompt before overwriting.
                -i,--interactive : prompt before overwriting.
                -u,--update : move only when the SOURCE file is newer than the DESTINATION file or when the DESTINATION is missing
                --help : Displays this help text""";
                return message;
            }
            else
                return "Error: mv requires two arguments at least";
        }
        parseOptions(arguments);

        if(arguments.size() == 2){
            return twoArguments(arguments.get(0), arguments.get(1), context);
        }
        else{
            return moreThanTwoArguments(arguments, context);
        }
    }

    public String twoArguments(String sourcePath, String destPath, InterpreterContext context){
        File sourceFile = relativeOrAbsolutePath(sourcePath, context);
        File destinationFile = relativeOrAbsolutePath(destPath, context);

        if (!sourceFile.exists()) {
            return "Error: Source file or directory does not exist.";
        }
        //handle -i
        if (destinationFile.exists() && interactive && !confirmOverwrite(destinationFile.getName())) {
            return "Operation canceled.";
        }
        //handle -u
        if (destinationFile.exists() && update && destinationFile.lastModified() >= sourceFile.lastModified()) {
            return "Destination is newer or same age of source.";
        }
        else if(!destinationFile.exists() && update) {
            destinationFile.mkdirs();
        }


        if (destinationFile.isDirectory() && isMove(destPath)) {
            destinationFile = new File(destinationFile, sourceFile.getName());
            try {
                //handle -f
                Files.move(sourceFile.toPath(), destinationFile.toPath(), force ? StandardCopyOption.REPLACE_EXISTING : StandardCopyOption.ATOMIC_MOVE);
                return "Moved \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".";
            }
            catch (IOException e) {
                return "Error: Unable to move \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".";
            }
        } 
        else {
            if(destinationFile.exists() && (force || interactive)){
                destinationFile.delete();
            }
            boolean rename = sourceFile.renameTo(destinationFile);
            if(rename){
                return "Renamed \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".";
            }
            else{
                return "Error: Unable to rename \"" + sourceFile.getPath() + "\" to \"" + destinationFile.getPath() + "\".";
            }
        }
    }

    public String moreThanTwoArguments(List<String> arguments, InterpreterContext context){
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
            //handle -i
            if (destinationFile.exists() && interactive && !confirmOverwrite(arguments.get(i))) {
                output.append("Operation skipped : ").append(arguments.get(i)).append("\n");
                continue;
            }
            //handle -u
            if (destinationFile.exists() && update && destinationFile.lastModified() >= sourceFile.lastModified()) {
                output.append("Error:").append(arguments.get(i)).append(" Destination is newer or same age of source.\n");
                continue;
            }
            else if(!destinationFile.exists() && isMove(destPath)) {
                destinationFile.mkdirs();
            }

            File targetFile = new File(destinationFile, sourceFile.getName());
            try {
                Files.move(sourceFile.toPath(), targetFile.toPath(), force ? StandardCopyOption.REPLACE_EXISTING : StandardCopyOption.ATOMIC_MOVE);
                output.append("Moved \"").append(sourceFile.getPath()).append("\" to \"").append(targetFile.getPath()).append("\".\n");
            } catch (IOException e) {
                output.append("Error: Unable to move \"").append(sourceFile.getPath()).append("\" to \"").append(destinationFile.getPath()).append("\".\n");
            }
        }
        return output.toString();
    }

    public boolean isMove(String argument) {
        return argument.endsWith("/") || argument.endsWith("\\");
    }

    private File relativeOrAbsolutePath(String path, InterpreterContext context) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(context.getCurrentDirectory(), path);
        }
        return file;
    }

    private void parseOptions(List<String> arguments) {
        while(true){
            String arg = arguments.get(0);
            if (arg.equals("-i") || arg.equals("--interactive")) {
                interactive = true;
                arguments.remove(0);
            } else if (arg.equals("-f") || arg.equals("--force")) {
                force = true;
                arguments.remove(0);
            }
            else if (arg.equals("-u") || arg.equals("--update")) {
                update = true;
                arguments.remove(0);
            }
            else{
                break;
            }
        }
    }

    private boolean confirmOverwrite(String fileName) {
        System.out.print("mv: overwrite \"" + fileName + "\"? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        boolean result = false; 
        if(input.equalsIgnoreCase("y")|| input.equalsIgnoreCase("Y"))
            result = true;
        //scanner.close();
        return result;
    }
}
