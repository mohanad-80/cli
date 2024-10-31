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

public class RmdirCommandTest {
  private InterpreterContext context;
  private RmdirCommand rmDirCommand;

  @TempDir
  File tempDir;

  @BeforeEach
  void setUp() {
    context = new InterpreterContext();
    context.setCurrentDirectory(tempDir.getAbsolutePath());
    rmDirCommand = new RmdirCommand();
  }

  @Test
  void testNoArguments() {
    Command command = new Command("rmdir", List.of());
    String result = rmDirCommand.execute(command, context);
    assertTrue(result.contains("Error: Usage: rmdir"));
  }

  @Test
  void testInvalidDirectoryName() {
    Command command = new Command("rmdir", List.of("invalid:name"));
    String result = rmDirCommand.execute(command, context);
    assertTrue(result.contains("Invalid directory name"));
  }

  @Test
  void testNonExistentDirectory() {
    Command command = new Command("rmdir", List.of("nonexistentDir"));
    String result = rmDirCommand.execute(command, context);
    assertTrue(result.contains("Directory does not exist"));
  }

  @Test
  void testDirectoryRemovedSuccessfully() throws IOException {
    File newDir = new File(tempDir, "newDir");
    Files.createDirectories(newDir.toPath());

    Command command = new Command("rmdir", List.of("newDir"));
    String result = rmDirCommand.execute(command, context);
    assertTrue(result.contains("Directory removed"));
  }

  @Test
  void testDirectoryThatIsNotEmpty() throws IOException {
    File newDir = new File(tempDir, "newDir");
    Files.createDirectories(newDir.toPath());
    File innerDir = new File(newDir, "innerDir");
    Files.createDirectories(innerDir.toPath());

    Command command = new Command("rmdir", List.of("newDir"));
    String result = rmDirCommand.execute(command, context);
    assertTrue(result.contains("Failed to remove directory"));
  }
}
