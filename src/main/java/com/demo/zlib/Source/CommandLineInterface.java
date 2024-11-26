package com.demo.zlib.Source;

import java.util.Scanner;

public class CommandLineInterface {
    private Scanner scanner;
    private CommandLineInterfaceBooks commandLineInterfaceBooks;
    private CommandLineInterfaceMember commandLineInterfaceMember;
    private LibraryBorrowingManagement libraryBorrowingManagement;

    public CommandLineInterface() {
        commandLineInterfaceBooks = new CommandLineInterfaceBooks();
        commandLineInterfaceMember = new CommandLineInterfaceMember();
        scanner = new Scanner(System.in);
    }

    public void displayMenu() {
        boolean flag = true;
        while (flag) {
            System.out.println(commandLineInterfaceBooks.getLibrary().getName() +", brings the whole world to your hands.");
            System.out.println("Library address: " + commandLineInterfaceBooks.getLibrary().getAddress());
            System.out.println("---------------------------------------------------");
            System.out.println("[0] Exit");
            System.out.println("[1] Manage Books");
            System.out.println("[2] Manage Members");
            System.out.println("[3] Manage Borrowings");
            String choice = scanner.nextLine();
            switch (choice) {
                case "0":
                    System.out.println("See you around.");
                    flag = false;
                    break;
                case "1":
                    commandLineInterfaceBooks.eventHandler();
                    break;
                case "2":
                    commandLineInterfaceMember.eventsHandler();
                    break;
                case "3":
                    libraryBorrowingManagement = new LibraryBorrowingManagement();
                    libraryBorrowingManagement.eventHandler();
                    break;
                default:
                    System.out.println("Invalid option. Please try again.");
                    break;
            }
        }
    }
}
