package interpreter.commands;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HelpCommandTest {

    private HelpCommand helpCommand;
    private final String expectedOutput = """
                
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

    @BeforeEach
    void setUp() {
        helpCommand = new HelpCommand();
    }

    @Test
    void testExecuteReturnsExpectedHelpText() {
        String result = helpCommand.execute();
        assertEquals(expectedOutput, result, "The execute method should return the expected help text.");
    }

    @Test
    void testExecuteConsistencyOnMultipleCalls() {
        // Multiple calls to check for consistency
        assertEquals(expectedOutput, helpCommand.execute(), "Help text should be consistent on multiple executions.");
        assertEquals(expectedOutput, helpCommand.execute(), "Help text should be consistent on multiple executions.");
        assertEquals(expectedOutput, helpCommand.execute(), "Help text should be consistent on multiple executions.");
    }

    @Test
    void testExecuteWithUnexpectedParameter() {
        // Hypothetically testing if the HelpCommand would fail gracefully if additional parameters were added
        String result = helpCommand.execute();
        assertEquals(expectedOutput, result, "Help command should return the correct output even if parameters were theoretically misused.");
    }

    @Test
    void testExecuteWithNullContext() {
        // Since execute() doesn't take any context, this just tests stability
        assertDoesNotThrow(() -> helpCommand.execute(), "HelpCommand should not throw errors even if invoked in unexpected ways.");
    }
}
