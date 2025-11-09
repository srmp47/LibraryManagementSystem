package library;
import library.controllers.CommandLineController;

public class Main {
    public static void main(String[] args) {
        CommandLineController cli = new CommandLineController();

        System.out.println("=== Library Management System ===");

        while (true) {
            cli.printMenu();
            String choice = cli.getUserInput();

            boolean shouldExit = cli.handleChoice(choice);
            if (shouldExit) {
                break;
            }
        }

        cli.exitProgram();
    }
}
