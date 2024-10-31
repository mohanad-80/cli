package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class TouchCommandTest {
    private InterpreterContext context;
    private TouchCommand touchCommand;

    @TempDir
    File tempDir;

    @BeforeEach
    void setUp() {
        context = new InterpreterContext();
        context.setCurrentDirectory(tempDir.getAbsolutePath());
        touchCommand = new TouchCommand();
    }

    @Test
    void testNoArguments() {
        Command command = new Command("touch", List.of());
        String result = touchCommand.execute(command, context);
        assertTrue(result.contains("Error: Usage: touch"));
    }

    @Test
    void testInvalidDirectoryName() {
        Command command = new Command("touch", List.of("invalid:name/test"));
        String result = touchCommand.execute(command, context);
        assertTrue(result.contains("Invalid directory name"));
    }

    @Test
    void testFileAlreadyExists() throws IOException {
        File dir = new File(tempDir, "test.txt");
        dir.createNewFile();

        Command command = new Command("touch", List.of("test.txt"));
        String result = touchCommand.execute(command, context);
        assertTrue(result.contains("File already exists"));
    }

    @Test
    void testFileCreatedSuccessfully() throws IOException {
        Command command = new Command("touch", List.of("newFileCreated"));
        String result = touchCommand.execute(command, context);
        assertTrue(result.contains("File created"));
    }

    @Test
    void testTheFileNameTooLong() throws IOException {
        Command command = new Command("touch", List.of("ozsokcrozuvfzeqxzuysrmplslxosafwldbgdryqlsmheomiw" +
                "yqgwryufopptmrzqbggpqtdduoigelnqjmhbpibafxdfbxigxuiozsokcrozuvfzeqxzuysrmplslxosafwldbgdry" +
                "qlsmheomiwyqgwryufopptmrzqbggpqtdduo" +
                "igelnqjmhbpibafxdfbxigxuiuiopdmnjmzynyffxqsqarmiawwyixizfabxssnyfwrenpfvbgtwsfavhqbot" +
                "mbhjroeqxqdfyvgzqejkttnhiwddwrjgewjdunxbcifzeeya" +
                "gtefkzskevrcreaievspzlshmmjzixevltxtnkuiqjcefikqmsfmbucjmlkokevnmu" +
                "nnwijwrrgkjknscqmdtwgnjqcm"));
        String result = touchCommand.execute(command, context);
        assertTrue(result.contains("Failed to create file"));
    }
}
