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

class MkdirCommandTest {
  private InterpreterContext context;
  private MkdirCommand mkdirCommand;

  @TempDir
  File tempDir;

  @BeforeEach
  void setUp() {
    context = new InterpreterContext();
    context.setCurrentDirectory(tempDir.getAbsolutePath());
    mkdirCommand = new MkdirCommand();
  }

  @Test
  void testNoArguments() {
    Command command = new Command("mkdir", List.of());
    String result = mkdirCommand.execute(command, context);
    assertTrue(result.contains("Error: Usage: mkdir"));
  }

  @Test
  void testInvalidDirectoryName() {
    Command command = new Command("mkdir", List.of("invalid:name"));
    String result = mkdirCommand.execute(command, context);
    assertTrue(result.contains("Invalid directory name"));
  }

  @Test
  void testDirectoryAlreadyExists() throws IOException {
    File existingDir = new File(tempDir, "existingDir");
    Files.createDirectories(existingDir.toPath());

    Command command = new Command("mkdir", List.of("existingDir"));
    String result = mkdirCommand.execute(command, context);
    assertTrue(result.contains("Directory already exists"));
  }

  @Test
  void testDirectoryCreatedSuccessfully() {
    Command command = new Command("mkdir", List.of("newDir"));
    String result = mkdirCommand.execute(command, context);
    assertTrue(result.contains("Directory created"));

    File newDir = new File(tempDir, "newDir");
    assertTrue(newDir.exists());
  }

  @Test
  void testTheDirectoryPathTooLong() throws IOException {
    Command command = new Command("mkdir", List.of("ozsokcrozuvfzeqxzuysrmplslxosafwldbgdryqlsmheomiw" +
        "yqgwryufopptmrzqbggpqtdduoigelnqjmhbpibafxdfbxigxuiozsokcrozuvfzeqxzuysrmplslxosafwldbgdry" +
        "qlsmheomiwyqgwryufopptmrzqbggpqtdduo" +
        "igelnqjmhbpibafxdfbxigxuiuiopdmnjmzynyffxqsqarmiawwyixizfabxssnyfwrenpfvbgtwsfavhqbot" +
        "mbhjroeqxqdfyvgzqejkttnhiwddwrjgewjdunxbcifzeeya" +
        "gtefkzskevrcreaievspzlshmmjzixevltxtnkuiqjcefikqmsfmbucjmlkokevnmu" +
        "nnwijwrrgkjknscqmdtwgnjqcm"));
    String result = mkdirCommand.execute(command, context);
    assertTrue(result.contains("Failed to create directory"));
  }
}