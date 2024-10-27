package interpreter.commands;

public class HelpCommand {
    public String execute() {
        return """

    Available Commands:
    -------------------
    pwd        : Prints the current working directory.

    cd [dir]   : Changes the current directory to [dir]. Use '..' to go up one level.

    ls         : Lists files and directories in the current directory.

    ls -a      : Lists all files and directories, including hidden ones.

    ls -r      : Lists files and directories recursively in all subdirectories.

    mkdir [dir]: Creates a new directory with the name [dir].

    rmdir [dir]: Removes an empty directory named [dir].

    touch [file]: Creates an empty file named [file] if it doesn't already exist.

    mv [src] [dest]: Moves or renames a file or directory from [src] to [dest].

    rm [file]  : Deletes the specified file.

    cat [file] : Displays the contents of the specified file.

    > [file]   : Redirects output to a file, overwriting the file if it exists.

    >> [file]  : Redirects output to a file, appending to the file if it exists.

    |          : Pipes the output of one command to another.

    exit       : Terminates the CLI session.

    help       : Displays this help information.

    -------------------
    """;
    }
}
