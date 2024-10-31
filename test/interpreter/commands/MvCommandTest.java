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
import static org.junit.jupiter.api.Assertions.assertTrue;

public class MvCommandTest {
  private MvCommand mvCommand;
  private InterpreterContext context;
  private File sourceFile;
  private File destFile;
  private File destDirectory;

  @BeforeEach
  void setUp() throws IOException {
    mvCommand = new MvCommand();
    context = new InterpreterContext();
    context.setCurrentDirectory(System.getProperty("java.io.tmpdir"));

    // Set up test files and directories
    sourceFile = new File(context.getCurrentDirectory(), "sourceFile.txt");
    destFile = new File(context.getCurrentDirectory(), "destFile.txt");
    destDirectory = new File(context.getCurrentDirectory(), "destDir");

    Files.createFile(sourceFile.toPath());
    Files.createDirectory(destDirectory.toPath());
  }

  @AfterEach
  void tearDown() throws IOException {
    Files.deleteIfExists(sourceFile.toPath());
    Files.deleteIfExists(destFile.toPath());
    Files.walk(destDirectory.toPath())
        .sorted((p1, p2) -> p2.compareTo(p1)) // Sort in reverse to delete files before directories
        .forEach(path -> {
          try {
            Files.deleteIfExists(path);
          } catch (IOException ignored) {
          }
        });
  }

  @Test
  void testMoveFileToDirectory() {
    Command command = new Command("mv", List.of(sourceFile.getPath(), destDirectory.getPath()));
    String result = mvCommand.execute(command, context);

    File movedFile = new File(destDirectory, sourceFile.getName());
    assertEquals("Moved \"" + sourceFile.getPath() + "\" to directory \"" + destDirectory.getPath() + "\".",
        result);
    assertTrue(movedFile.exists());
  }

  @Test
  void testMoveFileToFile() throws IOException {
    Files.createFile(destFile.toPath()); // Create destination file

    Command command = new Command("mv", List.of(sourceFile.getPath(), destFile.getPath()));
    String result = mvCommand.execute(command, context);

    assertEquals("Error: Unable to move/rename \"" + sourceFile.getPath() + "\" to \"" + destFile.getPath() + "\".",
        result);
    assertTrue(Files.exists(sourceFile.toPath()));
  }

  @Test
  void testRenameFile() {
    Command command = new Command("mv", List.of(sourceFile.getPath(), destFile.getPath()));
    String result = mvCommand.execute(command, context);

    assertEquals("Renamed \"" + sourceFile.getPath() + "\" to \"" + destFile.getPath() + "\".", result);
    assertTrue(destFile.exists());
    assertTrue(!sourceFile.exists());
  }

  @Test
  void testMoveMultipleFilesToDirectory() throws IOException {
    // Ensure sourceFile2 does not already exist
    File sourceFile2 = new File(context.getCurrentDirectory(), "sourceFile2.txt");
    Files.deleteIfExists(sourceFile2.toPath()); // Remove if exists

    // Now create the file
    Files.createFile(sourceFile2.toPath());

    Command command = new Command("mv",
        List.of(sourceFile.getPath(), sourceFile2.getPath(), destDirectory.getPath()));
    String result = mvCommand.execute(command, context);

    assertTrue(result.contains("Moved\"" + sourceFile.getPath() + "\" to \""
        + new File(destDirectory, sourceFile.getName()).getPath() + "\"."));
    assertTrue(result.contains("Moved\"" + sourceFile2.getPath() + "\" to \""
        + new File(destDirectory, sourceFile2.getName()).getPath() + "\"."));
    assertTrue(new File(destDirectory, sourceFile.getName()).exists());
    assertTrue(new File(destDirectory, sourceFile2.getName()).exists());
  }

  @Test
  void testSourceFileNotExist() {
    File nonExistentFile = new File(context.getCurrentDirectory(), "nonExistentFile.txt");
    Command command = new Command("mv", List.of(nonExistentFile.getPath(), destDirectory.getPath()));
    String result = mvCommand.execute(command, context);

    assertEquals("Error: Source file or directory does not exist.", result);
  }

  @Test
  void testDestinationNotDirectory() {
    Command command = new Command("mv", List.of(sourceFile.getPath(), sourceFile.getPath(), destFile.getPath()));
    String result = mvCommand.execute(command, context);

    assertEquals("Error: Destination is not a directory.", result);
  }
}
