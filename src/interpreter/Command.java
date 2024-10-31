package interpreter;

import java.util.List;

public class Command {
  private String name;
  private List<String> arguments;
  private String outputFile; // For redirection
  private boolean append; // `true` if `>>`, `false` if `>`
  private Command nextCommand; // For piping `|`
  private Boolean prompt; // For pwd in prompt
  private Boolean hasPreviousCommand;
  private Command previousCommand;

  public Command(String name, List<String> arguments) {
    this.name = name;
    this.arguments = arguments;
  }

  public String getName() {
    return name;
  }

  public List<String> getArguments() {
    return arguments;
  }

  public void setOutputFile(String outputFile, boolean append) {
    this.outputFile = outputFile;
    this.append = append;
  }

  public String getOutputFile() {
    return outputFile;
  }

  public boolean isAppend() {
    return append;
  }

  public Command getNextCommand() {
    return nextCommand;
  }

  public void setNextCommand(Command nextCommand) {
    this.nextCommand = nextCommand;
  }

  public void setPrompt(Boolean isPrompt) {
    this.prompt = isPrompt;
  }

  public Boolean isPrompt() {
    return prompt;
  }

  public Boolean hasPreviousCommand() {
    return this.hasPreviousCommand;
  }

  public void setPreviousCommand(Boolean hasPreviousCommand) {
    this.hasPreviousCommand = hasPreviousCommand;
  }

  public void setPreviousCommand(Command prevCommand) {
    previousCommand = prevCommand;
  }

  public Command getPreviousCommand() {
    return previousCommand;
  }
}
