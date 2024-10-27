package interpreter.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import interpreter.Command;
import interpreter.InterpreterContext;

public class CdCommandTest {
    private InterpreterContext context;
    private CdCommand cdCommand;

    @BeforeEach
    public void setUp() {
        context = new InterpreterContext();
        cdCommand = new CdCommand();
        context.setCurrentDirectory(System.getProperty("user.dir")); // Set to project root for testing
    }

    @Test
    public void testChangeToExistingDirectory() {
        Command command = new Command("cd", List.of("src")); // Assuming "src" exists in the project root
        String result = cdCommand.execute(command, context);
        assertEquals("Changed directory to: " + new File("src").getAbsolutePath(), result);
    }

    @Test
    public void testChangeToNonexistentDirectory() {
        Command command = new Command("cd", List.of("nonexistentDir"));
        String result = cdCommand.execute(command, context);
        assertEquals("Error: No such directory: " + new File("nonexistentDir").getAbsolutePath(), result);
    }

    @Test
    public void testEmptyArgument() {
        Command command = new Command("cd", List.of());
        String result = cdCommand.execute(command, context);
        assertEquals("Error: Usage: cd <directory>", result);
    }

    @Test
    public void testChangeToParentDirectory() {
        Command command = new Command("cd", List.of(".."));
        String result = cdCommand.execute(command, context);
        assertTrue(result.startsWith("Changed directory to: "));
    }
}
