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

class RmCommandTest {

    private RmCommand rmCommand;
    private InterpreterContext context;

    @BeforeEach
    void setUp() {
        rmCommand = new RmCommand();
        context = new InterpreterContext();
        context.setCurrentDirectory(System.getProperty("user.dir")); // Use project root for testing
    }

    @Test
    void testRemoveFile() throws IOException {
        // Create a temporary file to test removal
        Path tempFile = Files.createTempFile("tempFile", ".txt");
        
        // Run the command to remove the file
        Command command = new Command("rm", List.of(tempFile.toString()));
        String result = rmCommand.execute(command, context);

        // Check if the file was removed
        assertTrue(Files.exists(tempFile), "The file could not be removed.");
        assertFalse(Files.exists(tempFile), "The file should be removed.");
        assertEquals("Removed file: " + tempFile.toString(), result.trim());
    }

    @Test
    void testRemoveNonExistentFileWithoutForce() {
        Command command = new Command("rm", List.of("nonexistent.txt"));
        String result = rmCommand.execute(command, context);
        
        assertTrue(result.contains("Error: nonexistent.txt does not exist"));
    }

    @Test
    void testRemoveNonExistentFileWithForce() {
        Command command = new Command("rm", List.of("-f", "nonexistent.txt"));
        String result = rmCommand.execute(command, context);
        
        assertTrue(result.isEmpty(), "With force option, there should be no error for non-existent file.");
    }

    @Test
    void testRemoveDirectoryWithRecursiveOption() throws IOException {
        // Create a temporary directory with a file inside
        Path tempDir = Files.createTempDirectory("tempDir");
        Files.createFile(tempDir.resolve("fileInDir.txt"));

        // Run the command to remove the directory recursively
        Command command = new Command("rm", List.of("-r", tempDir.toString()));
        String result = rmCommand.execute(command, context);

        // Check if the directory was removed
        assertFalse(Files.exists(tempDir), "The directory and its contents should be removed.");
        assertEquals("Removed directory: " + tempDir.toString(), result.trim());
    }

    @Test
    void testRemoveEmptyDirectoryWithDirectoryOnlyOption() throws IOException {
        // Create an empty temporary directory
        Path tempDir = Files.createTempDirectory("emptyDir");

        // Run the command to remove the empty directory
        Command command = new Command("rm", List.of("-d", tempDir.toString()));
        String result = rmCommand.execute(command, context);

        // Check if the directory was removed
        assertFalse(Files.exists(tempDir), "The empty directory should be removed.");
        assertEquals("Removed directory: " + tempDir.toString(), result.trim());
    }

    @Test
    void testHelpMessage() {
        Command command = new Command("rm", List.of("--help"));
        String result = rmCommand.execute(command, context);

        assertTrue(result.contains("rm: Removes each given file"), "Help message should be displayed.");
        assertTrue(result.contains("-f"), "Help message should list -f option.");
        assertTrue(result.contains("-i"), "Help message should list -i option.");
        assertTrue(result.contains("-r"), "Help message should list -r option.");
        assertTrue(result.contains("-d"), "Help message should list -d option.");
    }

    @Test
    void testRemoveFileInteractiveYes() throws IOException {
        Path tempFile = Files.createTempFile("tempFileInteractiveYes", ".txt");

        System.setIn(new ByteArrayInputStream("y\n".getBytes()));

        Command command = new Command("rm", List.of(tempFile.toString()));
        String result = rmCommand.execute(command, context);

        assertTrue(Files.exists(tempFile), "File should not be removed after confirmation.");
        assertFalse(Files.exists(tempFile), "File should be removed after confirmation.");
        assertEquals("Removed file: " + tempFile.toString(), result.trim());
    }

    @Test
    void testRemoveFileInteractiveNo() throws IOException {
        Path tempFile = Files.createTempFile("tempFileInteractiveNo", ".txt");

        System.setIn(new ByteArrayInputStream("n\n".getBytes()));

        Command command = new Command("rm", List.of(tempFile.toString()));
        String result = rmCommand.execute(command, context);

        assertTrue(Files.exists(tempFile), "File should not be removed if deletion was skipped.");
        assertFalse(Files.exists(tempFile), "File should be removed if deletion was skipped.");
        assertEquals("Skipped " + tempFile.getFileName(), result.trim());
    }
}