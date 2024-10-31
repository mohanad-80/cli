package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

class MvCommandTest {
    private InterpreterContext context;
    private MvCommand mvCommand;

    @TempDir
    File tempDir;

    @BeforeEach
    void setup() {
        context = new InterpreterContext();
        context.setCurrentDirectory(tempDir.getAbsolutePath());
        mvCommand = new MvCommand();
    }

    @Test
    void testNoArguments() {
        Command command = new Command("mv", List.of());
        String result = mvCommand.execute(command, context);
        assertTrue(result.contains("Error: mv requires two arguments at least"));
    }

    @Test
    void testSourceFileDoesNotExist() {
        Command command = new Command("mv", List.of("nonExistentFile.txt", "destination.txt"));
        String result = mvCommand.execute(command, context);
        assertTrue(result.contains("Error: Source file or directory does not exist."));
    }

    @Test
    void testRenameFile() throws IOException {
        File sourceFile = new File(tempDir, "file.txt");
        sourceFile.createNewFile();

        Command command = new Command("mv", List.of("file.txt", "renamedFile.txt"));
        String result = mvCommand.execute(command, context);

        File renamedFile = new File(tempDir, "renamedFile.txt");
        assertTrue(result.contains("Renamed \"" + sourceFile.getPath() + "\" to \"" + renamedFile.getPath() + "\"."));
        assertTrue(renamedFile.exists());
    }

    @Test
    void testMoveFileToDirectory() throws IOException {
        File sourceFile = new File(tempDir, "file.txt");
        File destinationDir = new File(tempDir, "destinationDir");
        sourceFile.createNewFile();
        destinationDir.mkdir();

        Command command = new Command("mv", List.of("file.txt", "destinationDir"));
        String result = mvCommand.execute(command, context);

        File movedFile = new File(destinationDir, "file.txt");
        assertTrue(result.contains("Moved \"" + sourceFile.getPath() + "\" to directory \"" + destinationDir.getPath() + "\"."));
        assertTrue(movedFile.exists());
    }

    @Test
    void testMoveMultipleFilesToDirectory() throws IOException {
        File sourceFile1 = new File(tempDir, "file1.txt");
        File sourceFile2 = new File(tempDir, "file2.txt");
        File destinationDir = new File(tempDir, "destinationDir");
        sourceFile1.createNewFile();
        sourceFile2.createNewFile();
        destinationDir.mkdir();

        Command command = new Command("mv", List.of("file1.txt", "file2.txt", "destinationDir"));
        String result = mvCommand.execute(command, context);

        File movedFile1 = new File(destinationDir, "file1.txt");
        File movedFile2 = new File(destinationDir, "file2.txt");

        assertTrue(result.contains("Moved\""+ sourceFile1.getPath() + "\" to \"" + movedFile1.getPath() + "\"."));
        assertTrue(result.contains("Moved\""+ sourceFile2.getPath() + "\" to \"" + movedFile2.getPath() + "\"."));
        assertTrue(movedFile1.exists());
        assertTrue(movedFile2.exists());
    }

    @Test
    void testMoveToInvalidDirectory() throws IOException {
        File sourceFile = new File(tempDir, "file.txt");
        File invalidDir = new File(tempDir, "notADir.txt");
        sourceFile.createNewFile();
        invalidDir.createNewFile();

        Command command = new Command("mv", List.of("file.txt", "notADir.txt"));
        String result = mvCommand.execute(command, context);
        assertTrue(result.contains("Error: Unable to move/rename \"" + sourceFile.getPath() + "\" to \"" + invalidDir.getPath() + "\"."));
    }

    @Test
    void testNonExistentSourceFile() {
        // Destination directory
        File destinationDir = new File(tempDir, "destDir");
        destinationDir.mkdir();

        // Execute with a non-existent source file
        String result = mvCommand.moreThanTwoArguments(List.of("nonExistentFile.txt", destinationDir.getPath()), context);

        // Assert that the output contains the error message for the nonexistent file
        assertTrue(result.contains("Error: Source file \"nonExistentFile.txt\" does not exist"));
    }

    @Test
    void testMultipleNonExistentSourceFiles() {
        // Destination directory
        File destinationDir = new File(tempDir, "destDir");
        destinationDir.mkdir();

        // Execute with multiple non-existent source files
        String result = mvCommand.moreThanTwoArguments(List.of("file1.txt", "file2.txt", destinationDir.getPath()), context);

        // Assert that the output contains the error messages for both nonexistent files
        assertTrue(result.contains("Error: Source file \"file1.txt\" does not exist"));
        assertTrue(result.contains("Error: Source file \"file2.txt\" does not exist"));
    }

    @Test
    void testMixedExistingAndNonExistentSourceFiles() throws IOException {
        // Create one source file and leave the other non-existent
        File existingFile = new File(tempDir, "existingFile.txt");
        existingFile.createNewFile();
        
        // Destination directory
        File destinationDir = new File(tempDir, "destDir");
        destinationDir.mkdir();

        // Execute with one existing and one non-existent source file
        String result = mvCommand.moreThanTwoArguments(List.of("existingFile.txt", "nonExistentFile.txt", destinationDir.getPath()), context);

        // Assert that the output contains the correct success message for the existing file
        assertTrue(result.contains("Moved\"" + existingFile.getPath() + "\" to \"" + new File(destinationDir, existingFile.getName()).getPath() + "\""));
        // Assert that the output contains the error message for the nonexistent file
        assertTrue(result.contains("Error: Source file \"nonExistentFile.txt\" does not exist"));
    }
}
