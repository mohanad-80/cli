import interpreter.Command;
import interpreter.CommandParser;
import interpreter.CommandHandler;

import java.util.Scanner;

public class Main {
  public static void main(String[] args) {
    Scanner scanner = new Scanner(System.in);
    CommandParser parser = new CommandParser();
    CommandHandler handler = new CommandHandler();
    Command pwd = parser.parse("pwd");
    pwd.setPrompt(true);

    System.out.println("Welcome to the CLI (Type 'help' for available commands)");

    while (true) {
      handler.execute(pwd);
      System.out.print("> ");
      String input = scanner.nextLine();
      Command command = parser.parse(input);

      if (command != null) {
        if (command.getName() == "exit")
          scanner.close();
        handler.execute(command);
      } else {
        System.out.println("Invalid command. Type 'help' for available commands.");
      }
    }
  }
}
