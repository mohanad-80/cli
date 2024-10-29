package interpreter.commands;

import interpreter.InterpreterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.io.File;

class PwdCommandTest {
    private PwdCommand pwdCommand;
    private InterpreterContext context;

    @BeforeEach
    void setUp() {
        pwdCommand = new PwdCommand();
        context = new InterpreterContext();
    }

    @Test
    void testExecuteWithValidDirectory() {
        File validDirectory = new File("/some/valid/directory");
        context.setCurrentDirectory(validDirectory.getPath());

        String result = pwdCommand.execute(context);

        assertEquals(validDirectory.toString(), result, "The execute method should return the valid directory path.");
    }

    @Test
    void testExecuteWithRootDirectory() {
        File rootDirectory = new File("/");
        context.setCurrentDirectory(rootDirectory.getPath());

        String result = pwdCommand.execute(context);

        assertEquals(rootDirectory.toString(), result, "The execute method should return the root directory path.");
    }

    @Test
    void testExecuteWithHomeDirectory() {
        File homeDirectory = new File(System.getProperty("user.home"));
        context.setCurrentDirectory(homeDirectory.getPath());

        String result = pwdCommand.execute(context);

        assertEquals(homeDirectory.toString(), result, "The execute method should return the user's home directory path.");
    }

    @Test
    void testExecuteWithEmptyDirectoryPath() {
        File emptyDirectory = new File("");
        context.setCurrentDirectory(emptyDirectory.getPath());

        String result = pwdCommand.execute(context);

        assertEquals(emptyDirectory.toString(), result, "The execute method should handle an empty directory path.");
    }

    @Test
    void testExecuteWithNonexistentDirectory() {
        File nonexistentDirectory = new File("/nonexistent/directory");
        context.setCurrentDirectory(nonexistentDirectory.getPath());

        String result = pwdCommand.execute(context);

        assertEquals(nonexistentDirectory.toString(), result, "The execute method should return the path even if it does not exist.");
    }
}
