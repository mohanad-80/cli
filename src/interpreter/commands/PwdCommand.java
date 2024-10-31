package interpreter.commands;

import interpreter.InterpreterContext;

public class PwdCommand {
  public String execute(InterpreterContext context) {
    return context.getCurrentDirectory().toString();
  }
}
