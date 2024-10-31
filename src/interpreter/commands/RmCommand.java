package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import java.io.File;
import java.util.List;
import java.util.Scanner;

public class RmCommand {
    private boolean force = false;
    private boolean interactive = false;
    private boolean recursive = false;
    private boolean directoryOnly = false;

    public String execute(Command command, InterpreterContext context) {
        List<String> arguments = command.getArguments();
        
        if (arguments.isEmpty()) {
            return "Error: rm requires at least one argument";
        }

        parseOptions(arguments);


        if(arguments.size() == 1 && arguments.get(0).equals("--help")){
            String message = """
               rm: Removes each given file. By default, does not remove directories. 
               Use options to modify behavior:
               -f : Forces the removal of files or directories
               -i : Prompts for confirmation before removing
               -r : Removes directories and their content recursively
               -d : Removes empty directories
               --help : Displays this help text""";
            return message;
        }

        StringBuilder output = new StringBuilder();
        for (String arg : arguments) {
            File target = relativeOrAbsolutePath(arg, context);

            if (!target.exists() && !force) {
                output.append("Error: ").append(arg).append(" does not exist.\n");
                continue;
            }

            if (target.isDirectory() && !recursive && !directoryOnly) {
                output.append("Error: ").append(arg).append(" is a directory (use -r to remove directories).\n");
                continue;
            }

            output.append(deleteFile(target)).append("\n");
        }
        return output.toString();
    }

    private String deleteFile(File file) {
        if (!file.exists()) {
            return force ? "" : "Error: " + file.getPath() + " does not exist";
        }

        if (interactive && !confirmDeletion(file.getName())) {
            return "Skipped " + file.getName();
        }

        if (file.isDirectory() && recursive) {
            return deleteDirectory(file) ? "Removed directory: " + file.getPath() : "Error: Could not remove directory: " + file.getPath();
        }

        if (directoryOnly && file.isDirectory() && file.list().length == 0) {
            return file.delete() ? "Removed directory: " + file.getPath() : "Error: Could not remove directory: " + file.getPath();
        } else if (!directoryOnly && file.isFile()) {
            return file.delete() ? "Removed file: " + file.getPath() : "Error: Could not remove file: " + file.getPath();
        }

        return "Error: " + file.getPath() + " is not removable.";
    }

    private boolean deleteDirectory(File dir) {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    deleteDirectory(f);
                } else {
                    f.delete();
                }
            }
        }
        return dir.delete();
    }

    private boolean confirmDeletion(String fileName) {
        System.out.print("rm: remove \"" + fileName + "\"? (y/n): ");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine().trim();
        boolean result = false; 
        //scanner.close();
        if(input.equalsIgnoreCase("y")|| input.equalsIgnoreCase("Y"))
            result = true;
        return result;
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
            else if (arg.equals("-r") || arg.equals("--recursive")) {
                recursive = true;
                arguments.remove(0);
            }
            else if (arg.equals("-d") || arg.equals("--dir")) {
                directoryOnly = true;
                arguments.remove(0);
            }
            else{
                break;
            }
        }
    }

    private File relativeOrAbsolutePath(String path, InterpreterContext context) {
        File file = new File(path);
        if (!file.isAbsolute()) {
            file = new File(context.getCurrentDirectory(), path);
        }
        return file;
    }

}
