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

class RmCommandTest {
  private InterpreterContext context;
  private RmCommand rmCommand;

  @TempDir
  File tempDir;

  @BeforeEach
  void setup() {
    context = new InterpreterContext();
    context.setCurrentDirectory(tempDir.getAbsolutePath());
    rmCommand = new RmCommand();
  }

  @Test
  void testNoArguments() {
    Command command = new Command("rm", List.of());
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Error: rm requires at least one argument"));
  }

  @Test
  void testFileDoesNotExist() {
    Command command = new Command("rm", List.of("nonExistentFile.txt"));
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Error: nonExistentFile.txt does not exist."));
  }

  @Test
  void testRemoveDirectoryError() throws IOException {
    File dir = new File(tempDir, "directory");
    dir.mkdir();

    Command command = new Command("rm", List.of("directory"));
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Error: directory is a directory."));
  }

  @Test
  void testRemoveFileSuccessfully() throws IOException {
    File file = new File(tempDir, "file.txt");
    file.createNewFile();

    Command command = new Command("rm", List.of("file.txt"));
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Removed file: " + file.getPath()));
  }

  @Test
  void testMultipleFiles() throws IOException {
    File file1 = new File(tempDir, "file1.txt");
    File file2 = new File(tempDir, "file2.txt");
    file1.createNewFile();
    file2.createNewFile();

    Command command = new Command("rm", List.of("file1.txt", "file2.txt", "nonExistentFile.txt"));
    String result = rmCommand.execute(command, context);

    assertTrue(result.contains("Removed file: " + file1.getPath()));
    assertTrue(result.contains("Removed file: " + file2.getPath()));
    assertTrue(result.contains("Error: nonExistentFile.txt does not exist."));
  }

  @Test
  void testDeleteNonExistentFile() {
    String result = rmCommand.execute(new Command("rm", List.of("nonExistentFile.txt")), context);
    assertTrue(result.contains("Error: nonExistentFile.txt does not exist."));
  }

  @Test
  void testDeleteDirectory() {
    File dir = new File(tempDir, "directory");
    dir.mkdir();

    String result = rmCommand.execute(new Command("rm", List.of("directory")), context);
    assertTrue(result.contains("Error: directory is a directory."));
  }

  @Test
  void testDeleteFileSuccess() throws IOException {
    File file = new File(tempDir, "fileToDelete.txt");
    file.createNewFile();

    String result = rmCommand.execute(new Command("rm", List.of("fileToDelete.txt")), context);
    assertTrue(result.contains("Removed file: " + file.getPath()));
  }

  @Test
  void testDeleteFileFailure() {
    File undeletableFile = new File(tempDir, "undeletableFile.txt");
    try {
      undeletableFile.createNewFile();
      undeletableFile.setWritable(false); // Make file undeletable to simulate a deletion failure.
    } catch (IOException e) {
      e.printStackTrace();
    }

    String result = rmCommand.execute(new Command("rm", List.of("undeletableFile.txt")), context);
    assertTrue(result.contains("Error: Could not remove file: " + undeletableFile.getPath()));

    undeletableFile.setWritable(true); // Cleanup by making the file writable again
  }

  @Test
  void testRelativePathHandling() throws IOException {
    // Create a file in the temp directory
    File file = new File(tempDir, "fileRelative.txt");
    file.createNewFile();

    // Use relative path based on temp directory
    Command command = new Command("rm", List.of("fileRelative.txt"));
    String result = rmCommand.execute(command, context);

    assertTrue(result.contains("Removed file: " + file.getPath()));
  }

  @Test
  void testAbsolutePathHandling() throws IOException {
    // Create a file using an absolute path
    File file = new File(tempDir, "fileAbsolute.txt");
    file.createNewFile();

    // Use absolute path
    Command command = new Command("rm", List.of(file.getAbsolutePath()));
    String result = rmCommand.execute(command, context);

    assertTrue(result.contains("Removed file: " + file.getAbsolutePath()));
  }

  @Test
  void testDeleteFilePermissionDenied() throws IOException {
    // Create a file and make it read-only to simulate permission denied
    File file = new File(tempDir, "readOnlyFile.txt");
    file.createNewFile();
    file.setReadOnly();

    Command command = new Command("rm", List.of("readOnlyFile.txt"));
    String result = rmCommand.execute(command, context);

    assertTrue(result.contains("Error: Could not remove file: " + file.getPath()));
  }

  @Test
  void testDeleteDirectoryInsteadOfFile() {
    File directory = new File(tempDir, "testDirectory");
    directory.mkdir();

    Command command = new Command("rm", List.of("testDirectory"));
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Error: testDirectory is a directory."));
  }

  @Test
  void testDeleteReadOnlyFile() throws IOException {
    File readOnlyFile = new File(tempDir, "readOnlyFile.txt");
    readOnlyFile.createNewFile();
    readOnlyFile.setWritable(false); // Make the file read-only

    Command command = new Command("rm", List.of("readOnlyFile.txt"));
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Error: Could not remove file: " + readOnlyFile.getPath()));

    readOnlyFile.setWritable(true); // Clean up by making it writable for deletion
  }

  @Test
  void testDeleteWritableFileFailure() throws IOException {
    File file = new File(tempDir, "lockedFile.txt");
    file.createNewFile();
    file.setWritable(true);

    // Simulate deletion failure by temporarily locking the file (if supported)
    try (var fos = new java.io.FileOutputStream(file)) {
      Command command = new Command("rm", List.of("lockedFile.txt"));
      String result = rmCommand.execute(command, context);
      assertTrue(result.contains("Error: Could not remove file: " + file.getPath()));
    } finally {
      // Clean up: release the lock by closing the FileOutputStream
      file.delete();
    }
  }

  @Test
  void testDeleteFileWithSuccessfulRemoval() throws IOException {
    File file = new File(tempDir, "deletableFile.txt");
    file.createNewFile();
    file.setWritable(true);

    Command command = new Command("rm", List.of("deletableFile.txt"));
    String result = rmCommand.execute(command, context);
    assertTrue(result.contains("Removed file: " + file.getPath()));
  }
}
