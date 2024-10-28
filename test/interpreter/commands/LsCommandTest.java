package interpreter.commands;

import java.util.List;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.stream.Collectors;

import static org.junit.Assert.assertFalse;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;

import interpreter.Command;
import interpreter.InterpreterContext;

import java.nio.file.Files;
import java.nio.file.Path;

public class LsCommandTest {
  /*
   * test with only ls
   * test with -a
   * test with -r
   * test with -a -r
   * test with path
   * test with -a -r path
   * test with path that does not exit
   * test with path not a directory
   * test with empty directory
   */
  private InterpreterContext context;
  private LsCommand lsCommand;

  @BeforeEach
  public void setUp() {
    context = new InterpreterContext();
    lsCommand = new LsCommand();
    context.setCurrentDirectory(System.getProperty("user.dir")); // Set to project root for testing
  }

  @Test
  public void testListingContent() {
    Command command = new Command("ls", List.of());
    String result = lsCommand.execute(command, context);
    assertTrue(result.contains("src")); // assuming src exist in the root
  }

  @Test
  public void testListingContentWithShowAllFlag() {
    Command command = new Command("ls", List.of("-a"));
    String result = lsCommand.execute(command, context);

    String[] expectedFiles = new File(context.getCurrentDirectory()).list();
    String expectedOutput = String.join("\n", expectedFiles) + "\n";

    assertTrue(result.contains("src")); // assuming src exist in the root
    assertTrue(result.startsWith(".")); // assuming root dir contains hidden files
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testListingContentWithRecursiveFlag() {
    Command command = new Command("ls", List.of("-r"));
    String result = lsCommand.execute(command, context);

    String[] expectedFiles = new File(context.getCurrentDirectory()).list();
    String[] filteredFiles = Arrays.stream(expectedFiles)
        .filter(file -> !file.startsWith("."))
        .toArray(String[]::new); // Collect into an array

    List<String> fileList = Arrays.asList(filteredFiles);
    Collections.reverse(fileList); // Simply reverse the order

    // Join the expected output into a single string with new lines
    String expectedOutput = String.join("\n", fileList) + "\n";

    assertTrue(result.contains("src")); // assuming src exist in the root
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testListingContentWithRecursiveAndShowAllFlags() {
    Command command = new Command("ls", List.of("-r", "-a"));
    String result = lsCommand.execute(command, context);

    String[] expectedFiles = new File(context.getCurrentDirectory()).list();

    List<String> fileList = Arrays.asList(expectedFiles);
    Collections.reverse(fileList);

    String expectedOutput = String.join("\n", fileList) + "\n";

    assertTrue(result.contains("src")); // assuming src exist in the root
    assertFalse(result.startsWith("."));
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testListingContentWithPath() {
    Command command = new Command("ls", List.of(System.getProperty("user.home")));
    String result = lsCommand.execute(command, context);

    String[] expectedFiles = new File(System.getProperty("user.home")).list();
    String[] filteredFiles = Arrays.stream(expectedFiles)
        .filter(file -> !file.startsWith("."))
        .toArray(String[]::new); // Collect into an array

    String expectedOutput = String.join("\n", filteredFiles) + "\n";

    assertFalse(result.startsWith("."));
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testListingContentWithRecursiveAndShowAllFlagsAndPath() {
    Command command = new Command("ls", List.of("-r", "-a", System.getProperty("user.home")));
    String result = lsCommand.execute(command, context);

    String[] expectedFiles = new File(System.getProperty("user.home")).list();

    List<String> fileList = Arrays.asList(expectedFiles);
    Collections.reverse(fileList);

    String expectedOutput = String.join("\n", fileList) + "\n";

    assertFalse(result.startsWith("."));
    assertEquals(expectedOutput, result);
  }

  @Test
  public void testListingContentWithNonexistingPath() {
    Command command = new Command("ls", List.of("nonexisting/path"));
    String result = lsCommand.execute(command, context);

    assertEquals("Error: Directory does not exist: " + new File("nonexisting/path"), result);
  }

  @Test
  public void testListingContentWithPathToFile() {
    Command command = new Command("ls", List.of("src/Main.java")); // assuming src exist in the root
    String result = lsCommand.execute(command, context);

    assertEquals("Error: Not a directory: " + new File("src/Main.java"), result);
  }

  @Test
  public void testListingContentWithPathToEmptyDirectory() throws IOException {
    // Create a temporary empty directory
    Path emptyDir = Files.createTempDirectory("emptyTestDir");
    
    Command command = new Command("ls", List.of(emptyDir.toString())); // assuming src exist in the root
    String result = lsCommand.execute(command, context);

    assertEquals("Directory is empty.", result);

    // Clean up the temporary directory
    Files.deleteIfExists(emptyDir);
  }
}
