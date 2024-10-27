package interpreter.commands;

public class ExitCommand {
  public void execute() {
    System.out.println("Exiting CLI...");
    System.exit(0);
  }
}
