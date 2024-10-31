package interpreter.commands;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import interpreter.Command;
import interpreter.InterpreterContext;

public class MvCommandTest {
    private MvCommand mvCommand;
    private InterpreterContext context;

    @BeforeEach
    public void setUp() {
        mvCommand = new MvCommand();
        context = new InterpreterContext();
        context.setCurrentDirectory(System.getProperty("user.dir"));
    }

    @Test
    public void testMoveFileSuccessfully() throws IOException {
        Path srcFile = Files.createTempFile("srcFile", ".txt");
        Path destFile = srcFile.resolveSibling("destFile.txt");

        Command command = new Command("mv", List.of(srcFile.toString(), destFile.toString()));
        String result = mvCommand.execute(command, context);

        assertTrue(Files.exists(destFile), "Destination file should exist after move.");
        assertFalse(Files.exists(srcFile), "Source file should no longer exist after move.");
        assertEquals("File moved successfully.", result);
    }

    @Test
    public void testMoveFileWithForceOption() throws IOException {
        Path srcFile = Files.createTempFile("srcFile", ".txt");
        Path destFile = Files.createTempFile("destFile", ".txt");

        Command command = new Command("mv", List.of("-f", srcFile.toString(), destFile.toString()));
        String result = mvCommand.execute(command, context);

        assertTrue(Files.exists(destFile), "Destination file should exist after forced move.");
        assertFalse(Files.exists(srcFile), "Source file should no longer exist after forced move.");
        assertEquals("File moved successfully with force.", result);
    }

    @Test
    public void testMoveFileInteractiveOption() throws IOException {
        Path srcFile = Files.createTempFile("srcFile", ".txt");
        Path destFile = Files.createTempFile("destFile", ".txt");

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));

        Command command = new Command("mv", List.of("-i", srcFile.toString(), destFile.toString()));
        String result = mvCommand.execute(command, context);

        assertTrue(Files.exists(destFile), "Destination file should exist after interactive move.");
        assertFalse(Files.exists(srcFile), "Source file should no longer exist after interactive move.");
        assertEquals("File moved successfully with confirmation.", result);
    }

    @Test
    public void testMoveFileUpdateOptionNewerSource() throws IOException {
        Path destFile = Files.createTempFile("destFile", ".txt");
        Path srcFile = Files.createTempFile("srcFile", ".txt");

        Command command = new Command("mv", List.of("-u", srcFile.toString(), destFile.toString()));
        String result = mvCommand.execute(command, context);

        assertTrue(Files.exists(destFile), "Destination file should exist after update move.");
        assertFalse(Files.exists(srcFile), "Source file should no longer exist after update move.");
        assertEquals("File moved with update option.", result);
    }

    @Test
    public void testMoveFileUpdateOptionOlderSource() throws IOException {
        Path srcFile = Files.createTempFile("srcFile", ".txt");
        Path destFile = Files.createTempFile("destFile", ".txt");

        Command command = new Command("mv", List.of("-u", srcFile.toString(), destFile.toString()));
        String result = mvCommand.execute(command, context);

        assertTrue(Files.exists(srcFile), "Source file should remain unchanged with older timestamp.");
        assertTrue(Files.exists(destFile), "Destination file should remain unchanged with older timestamp.");
        assertEquals("File move skipped: destination file is newer.", result);
    }

    @Test
    public void testMoveFileWithHelpOption() {
        Command command = new Command("mv", List.of("--help"));
        String result = mvCommand.execute(command, context);

        assertTrue(result.contains("Usage: mv [options]"), "Help option should provide usage information.");
    }
}
