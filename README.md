# CS341 - Operating Systems: Command Line Interpreter

**Faculty of Computers and Artificial Intelligence, Cairo University**

This repository contains the collaborative project for the course **CS341 - Operating Systems**. Our task is to implement a **Command Line Interpreter** (CLI) using Java.

## Project Overview

The goal of this project is to create a basic command-line interface (CLI) capable of interpreting and executing basic shell commands such as `cd`, `echo`, `cat`, as well as handling operators like `>>` (redirection) and `|` (piping).

The project helps us apply concepts learned in the Operating Systems course, including:

- Command parsing and execution
- File system interaction
- Process handling and inter-process communication (e.g., pipes)

## Repository Structure

```
.
├── src/
│   ├── interpreter/
│   │   ├── Commands/                   # Directory containing classes for all commands
│   │   │   ├── CdCommand.java          # Class containing the implementation of cd command
│   │   │   ├── LsCommand.java          # Class containing the implementation of ls command
│   │   │   ├── ...
│   │   ├── Command.java                # Command class representing parsed commands
│   │   ├── CommandHandler.java         # Executes commands like cd, echo, etc.
│   │   ├── CommandParser.java          # Parses the input commands
│   │   ├── InterpreterContext.java     # For state management
│   └── Main.java                       # Main class to run the CLI
├── README.md                           # Project documentation
├── .gitignore                          # Ignore unnecessary files
```

- **`src/interpreter/CommandParser.java`**: This class is responsible for parsing the input strings into command names, arguments, and handling special operators like `>>` and `|`.
- **`src/interpreter/CommandHandler.java`**: Executes parsed commands (like `cd`, `echo`, etc.) and handles special operators such as output redirection or piping.

- **`src/interpreter/Command.java`**: Represents the parsed command and its associated details (e.g., name, arguments, operator, target).

- **`src/Main.java`**: The entry point of the program. It runs the CLI and passes the user input to the parser and handler.

## Usage

1. **Clone the repository**:

   ```bash
   git clone https://github.com/mohanad-80/command_line_interpreter.git
   cd command_line_interpreter
   ```

2. **Compile the program**:

   ```bash
   javac src/Main.java
   ```

3. **Run the CLI**:

   ```bash
   java src.Main
   ```

4. **Example Commands**:
   - Change directory: `cd path/to/directory`
   - Display message: `echo "Hello World"`
   - Append to a file: `echo "Hello World" >> output.txt`
   - Pipe commands: `cat file.txt | grep "pattern"`

## Collaboration and Guidelines

We will follow these guidelines for collaboration:

1. **Branching**: Each member should create their own branch when working on new features or fixes.

   - Branch from the `main` branch: `git checkout -b feature/your-feature-name dev`
   - Push changes to your branch: `git push origin feature/your-feature-name`
   - Submit a pull request (PR) for review.

2. **Code Style**: Please follow Java conventions, with clear variable names and comments for complex logic.

3. **Pull Requests**:
   - Ensure your code compiles and passes any tests before submitting.

## License

This project is licensed under the MIT License.

---

### Contributors

- **[Mohanad Ahmed](https://github.com/mohanad-80)**
- **[Omar Abdelmonem](https://github.com/OmarAbdelmonemSayed)**
- **[Ahmed Ehab](https://github.com/AhmedEhab2022)**
- **[Ahmed Ammer](https://github.com/Ahmed-Amer01)**
