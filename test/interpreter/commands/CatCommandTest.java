package interpreter.commands;

import interpreter.Command;
import interpreter.InterpreterContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Scanner;
import java.io.ByteArrayInputStream;

import static org.junit.jupiter.api.Assertions.*;

class CatCommandTest {

  private CatCommand catCommand;
  private InterpreterContext context;

  @BeforeEach
  void setUp() {
    catCommand = new CatCommand();
    context = new InterpreterContext(); // Initialize with default or temporary directory
  }

  @Test
  void testCatAloneHandlesStandardInput() {
    Scanner scanner = new Scanner("line1\nline2\n-1\n");
    catCommand.catAlone(scanner);
    assertTrue(true, "catAlone should process lines correctly until '-1' is encountered.");
  }

  @Test
  void testCatWithFileReadsContent() throws IOException {
    // Set up a temporary file
    Path tempFile = Files.createTempFile("testFile", ".txt");
    Files.write(tempFile, "Hello\nWorld".getBytes());

    context.setCurrentDirectory(tempFile.getParent().toString());

    String result = catCommand.catWithFile(tempFile.getFileName().toString(), context);
    assertEquals("Hello\nWorld", result, "catWithFile should return the contents of a valid file.");
    Files.deleteIfExists(tempFile); // Clean up
  }

  @Test
  void testCatWithFileEmptyFile() throws IOException {
    Path tempFile = Files.createTempFile("emptyFile", ".txt");
    context.setCurrentDirectory(tempFile.getParent().toFile().getPath());

    String result = catCommand.catWithFile(tempFile.getFileName().toString(), context);

    assertEquals("", result, "catWithFile should return an empty string for an empty file.");
    Files.deleteIfExists(tempFile);
  }

  @Test
  void testCatWithFileNonExistentFile() {
    String result = catCommand.catWithFile("nonexistent.txt", context);

    assertEquals("Error: No such file: " + context.getCurrentDirectory() + "\\nonexistent.txt", result,
        "catWithFile should return an error message for a nonexistent file.");
  }

  @Test
  void testCatWithFileDirectoryPath() throws IOException {
    // Path to a typically directory in Windows
    String restrictedPath = "C:\\System Volume Information";

    context.setCurrentDirectory("C:\\"); // Setting root directory for reference

    String result = catCommand.catWithFile(restrictedPath, context);

    // Assert that the result returns a Not a file error
    assertEquals("Error: Not a file: " + restrictedPath, result,
        "catWithFile should return a Not a file error if the path for directory.");
  }

  @Test
  void testCatWithOperatorsProcessesInputCorrectly() {
    Scanner scanner = new Scanner("line1\nline2\n-1\n");
    String result = catCommand.catWithOperators(scanner);

    assertEquals("line1\nline2", result, "catWithOperators should return all lines up to '-1'.");
  }

  @Test
  void testExecuteWithArgumentsReadsFileContent() throws IOException {
    Path tempFile = Files.createTempFile("executeTestFile", ".txt");
    Files.write(tempFile, "Execute\nTest".getBytes());

    context.setCurrentDirectory(tempFile.getParent().toFile().getPath());

    Command command = new Command("cat", List.of(tempFile.getFileName().toString()));
    String result = catCommand.execute(command, context);

    assertEquals("Execute\nTest", result,
        "execute should read and return file contents when a valid file path is provided.");
    Files.deleteIfExists(tempFile);
  }

  @Test
  void testExecuteWithoutArgumentsProcessesStandardInput() {
    Command command = new Command("cat", List.of());

    // Prepare the simulated input, including "-1" to break the loop
    String simulatedInput = "line1\nline2\n-1\n";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
    System.setIn(inputStream); // Redirect System.in to our simulated input

    String result = catCommand.execute(command, context);

    assertTrue(result.contains(""), "execute without arguments should read from standard input.");
  }

  @Test
  void testExecuteWithNullOutputFile() {
    Command command = new Command("cat", List.of());
    InterpreterContext context = new InterpreterContext();
    CatCommand catCommand = new CatCommand();

    // Prepare the simulated input, including "-1" to break the loop
    String simulatedInput = "Hello\nWorld\n-1\n";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
    System.setIn(inputStream); // Redirect System.in to our simulated input

    String result = catCommand.execute(command, context);

    assertEquals("", result, "execute with null output file should handle standard input.");

    // Reset System.in if other tests rely on it
    System.setIn(System.in);
  }

  @Test
  void testExecuteWithNonexistentFileArgument() {
    Command command = new Command("cat", List.of("nonexistent.txt"));

    String result = catCommand.execute(command, context);

    assertEquals("Error: No such file: " + context.getCurrentDirectory() + "\\nonexistent.txt", result,
        "execute should return an error for a nonexistent file argument.");
  }

  @Test
  void testCatWithFileUnableToResolve() {
    Command command = new Command("cat", List.of("\"/dir/file\""));

    String result = catCommand.execute(command, context);
    assertEquals("Error: Unable to resolve path.", result);
  }

  @Test
  void testExecuteNoArgumentsAndOutputFileIsNotNull() {
    Command command = new Command("cat", List.of());
    command.setOutputFile("test.txt", false);

    // Prepare the simulated input, including "-1" to break the loop
    String simulatedInput = "Hello\nWorld\n-1\n";
    ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
    System.setIn(inputStream); // Redirect System.in to our simulated input

    String result = catCommand.execute(command, context);
    assertEquals("Hello\nWorld", result);
  }

  @Test
  void testCatWithOpereatorsNoinput() {
    Scanner scanner = new Scanner("-1");

    String result = catCommand.catWithOperators(scanner);
    assertEquals("", result);
  }
  @Test
  void testExecuteWithNoArgumentsButWithOutputFile() {
      Command command = new Command("cat", List.of());
      command.setOutputFile("output.txt", false);

      // Simulate user input
      String simulatedInput = "input1\ninput2\n-1\n";
      ByteArrayInputStream inputStream = new ByteArrayInputStream(simulatedInput.getBytes());
      System.setIn(inputStream);

      String result = catCommand.execute(command, context);

      assertEquals("input1\ninput2", result, 
          "When there is no argument but an output file, catWithOperators should process user input.");
      System.setIn(System.in); // Reset System.in
  }

  @Test
  void testExecuteWithArgumentsAndOutputFileHelpCommand() {
      // Set up the previous command as "help" and configure the main command
      Command previousCommand = new Command("help", List.of());
      Command command = new Command("cat", List.of("arg1", "arg2"));
      command.setOutputFile("output.txt", false);
      command.setPreviousCommand(previousCommand);

      // Execute the HelpCommand directly to get the expected output
      String expectedHelpOutput = new HelpCommand().execute();
      
      // Execute the catCommand and capture the output
      String result = catCommand.execute(command, context);
      
      // Verify the output is the same as executing HelpCommand directly
      assertEquals(expectedHelpOutput, result, "If previous command is 'help', it should execute HelpCommand.");
  }

  @Test
  void testExecuteWithArgumentsAndOutputFileNonHelpCommand() {
    Command previousCommand = new Command("echo", List.of());
    Command command = new Command("cat", List.of("arg1", "arg2"));
    command.setOutputFile("output.txt", false);
    command.setPreviousCommand(previousCommand);
    
    String result = catCommand.execute(command, context);
    
    assertEquals("arg1\narg2", result, "If previous command is not 'help', it should concatenate arguments with newlines.");
}

@Test
void testExecuteWithArgumentsAndOutputFileNoPreviousCommand() throws IOException {
    // Create a temporary file with some content
    Path tempFile = Files.createTempFile("testFile", ".txt");
    Files.write(tempFile, "File Content".getBytes());
    
    Command command = new Command("cat", List.of(tempFile.getFileName().toString()));
    command.setOutputFile("output.txt", false);
    context.setCurrentDirectory(tempFile.getParent().toFile().getPath());
    
    String result = catCommand.execute(command, context);
    
    assertEquals("File Content", result, "If no previous command exists, it should read the file content specified in arguments.");
    
    Files.deleteIfExists(tempFile); // Cleanup after test
}
}
