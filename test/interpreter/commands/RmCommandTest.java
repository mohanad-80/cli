package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

class RmCommandTest {
  private RmCommand rmCommand;
  private InterpreterContext context;
  private File testFile;
  private File testDirectory;

  @BeforeEach
  void setUp() throws IOException {
    rmCommand = new RmCommand();
    context = new InterpreterContext();

    // Set up a temporary directory and file in the context's directory
    testFile = new File(context.getCurrentDirectory(), "testFile.txt");
    Files.createFile(testFile.toPath());

    testDirectory = new File(context.getCurrentDirectory(), "testDirectory");
    Files.createDirectory(testDirectory.toPath());
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.deleteIfExists(testFile.toPath());
    Files.walk(testDirectory.toPath())
        .sorted((p1, p2) -> p2.compareTo(p1)) // Sort in reverse to delete directory contents first
        .forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException ignored) {
          }
        });
  }

  @Test
  void testRemoveExistingFile() {
    Command command = new Command("rm", List.of(testFile.getPath()));
    String result = rmCommand.execute(command, context);

    assertEquals("Removed file: " + testFile.getPath() + "\n", result);
    assertFalse(testFile.exists(), "File should be deleted");
  }

  @Test
  void testRemoveNonExistentFile() {
    Command command = new Command("rm", List.of("nonExistentFile.txt"));
    String result = rmCommand.execute(command, context);

    assertEquals("Error: nonExistentFile.txt does not exist.\n", result);
  }

  @Test
  void testRemoveDirectory() {
    Command command = new Command("rm", List.of(testDirectory.getPath()));
    String result = rmCommand.execute(command, context);

    assertEquals("Error: " + testDirectory.getPath() + " is a directory.\n", result);
  }

  @Test
  void testNoArguments() {
    Command command = new Command("rm", List.of());
    String result = rmCommand.execute(command, context);

    assertEquals("Error: rm requires at least one argument", result);
  }
}
