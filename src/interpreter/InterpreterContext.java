package interpreter;

public class InterpreterContext {
  private String currentDirectory;

  public InterpreterContext() {
    this.currentDirectory = System.getProperty("user.home"); // Initial directory
  }

  public String getCurrentDirectory() {
    return currentDirectory;
  }

  public void setCurrentDirectory(String currentDirectory) {
    this.currentDirectory = currentDirectory;
  }
}
