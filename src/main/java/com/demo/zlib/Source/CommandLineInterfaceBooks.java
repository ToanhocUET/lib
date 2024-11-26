package com.demo.zlib.Source;

import java.util.HashMap;
import java.util.Scanner;
public class CommandLineInterfaceBooks {
    private Scanner scanner = new Scanner(System.in);
    private Library library = new Library();
    private LibraryBookManagement libraryBookManagement = new LibraryBookManagement();

    public Library getLibrary() {
        return library;
    }

    public void displayMenu() {
        System.out.println("-----------------------------------------");
        System.out.println("[0] Exit");
        System.out.println("[1] Add Book");
        System.out.println("[2] Remove Book");
        System.out.println("[3] Search Book");
        System.out.println("[4] Update Book");
        System.out.println("[5] Display All Book");
        System.out.println("------------------------------------------");
    }

    //Các phương thức tìm kiếm
    public boolean tryAgain() {
        System.out.println("y : Yes");
        System.out.println("n : No. Back to main menu");
        String answer = scanner.nextLine();
        return answer.equals("y");
    }


    public void searchByCriteria() {
        boolean searchFlag = true;
        while (searchFlag) {
            System.out.println("Which criteria would you like to search?");
            System.out.println("1. Search by ISBN");
            System.out.println("2. Search by title");
            System.out.println("3. Search by author");
            System.out.println("4. Search by category");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    System.out.println("Enter the ISBN of the book you want to search>> ");
                    String isbn = scanner.nextLine();
                    libraryBookManagement.searchBooksByISBN(isbn);
                    libraryBookManagement.setBookMap(new HashMap<>());
                    System.out.println("Do you want to search another one?");
                    searchFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.print("Enter the title of the book you want to search>> ");
                    String userTitle = scanner.nextLine();
                    libraryBookManagement.searchBooksByTitle(userTitle);
                    libraryBookManagement.setBookMap(new HashMap<>());
                    System.out.println("Do you want to search another one?");
                    searchFlag = this.tryAgain();
                    break;
                case "3":
                    System.out.print("Enter the author of the book you want to search>> ");
                    String userAuthor = scanner.nextLine();
                    libraryBookManagement.searchBooksByAuthor(userAuthor);
                    libraryBookManagement.setBookMap(new HashMap<>());
                    System.out.println("Do you want to search another one?");
                    searchFlag = this.tryAgain();
                    break;
                case "4":
                    System.out.print("Enter the category of the book you want to search>> ");
                    String category = scanner.nextLine();
                    libraryBookManagement.searchBooksByCategory(category);
                    libraryBookManagement.setBookMap(new HashMap<>());
                    System.out.println("Do you want to search another one?");
                    searchFlag = this.tryAgain();
                    break;
                default:
                    System.out.println("Invalid input. Try again (y) or press 0 to back to main menu");
                    String userChoice = scanner.nextLine();
                    if (userChoice.equals("0")) {
                        searchFlag = false;
                    } else {
                        searchFlag = true;
                    }
                    break;
            }
        }
    }

    //Casc Phương thức xóa sách
    public String bookISBNByOrder(int order) {
        Book book = libraryBookManagement.getBookByOrder(libraryBookManagement.getBookMap(), order);
        return book.getISBN();
    }

    public void removeBookByCriteria() {
        boolean removeFlag = true;
        while (removeFlag) {
            System.out.println("Which remove's criteria do you want to choose?");
            System.out.println("1. Remove By ISBN");
            System.out.println("2. Remove By Title");
            System.out.println("3. Remove By Author");
            System.out.println("4. Remove By Category");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    System.out.println("Enter the ISBN of the book you want to remove: ");
                    String ISBN = scanner.nextLine();
                    libraryBookManagement.removeBooksByISBN(ISBN);
                    System.out.println("Do you want to remove another one?");
                    removeFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.print("Enter the title of the book you want to remove: ");
                    String userTitle = scanner.nextLine();
                    libraryBookManagement.searchBooksByTitle(userTitle);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Which book do you want to remove?");
                        int userTitleOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.removeBooksByISBN(this.bookISBNByOrder(userTitleOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    }
                case "3":
                    System.out.print("Enter the author of the book you want to remove: ");
                    String userAuthor = scanner.nextLine();
                    libraryBookManagement.searchBooksByAuthor(userAuthor);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Which book do you want to remove?");
                        int userAuthorOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.removeBooksByISBN(this.bookISBNByOrder(userAuthorOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    }
                case "4":
                    System.out.print("Enter the category of the book you want to remove: ");
                    String userCategory = scanner.nextLine();
                    libraryBookManagement.searchBooksByCategory(userCategory);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to remove?");
                        int userCategoryOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.removeBooksByISBN(this.bookISBNByOrder(userCategoryOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    }
                default:
                    System.out.println("Invalid input. Try again (y) or press 0 to back to main menu");
                    String userChoice = scanner.nextLine();
                    if (userChoice.equals("0")) {
                        removeFlag = false;
                    } else {
                        removeFlag = removeFlag;
                    }
                    break;
            }
        }
    }

    //Phương thức hiển thị tất cả các sách
    public void displayAllBooks() {
        boolean displayFlag = true;
        while (displayFlag) {
            libraryBookManagement.displayAllBooks();
            while (true) {
                System.out.println("Press 0 to back to main menu.");
                String userChoice = scanner.nextLine();
                if (userChoice.equals("0")) {
                    displayFlag = false;
                    break;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }
    }

    //Các phương thức cập nhật thuộc tính cho sách
    public void modifyBookByCriteria() {
        boolean modifyFlag = true;
        while (modifyFlag) {
            System.out.println("Which search criteria do you want to choose?");
            System.out.println("1. Search Books By ISBN");
            System.out.println("2. Search Books By Title");
            System.out.println("3. Search Books By Author");
            System.out.println("4. Search Books By Category");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    System.out.println("Enter the ISBN of the book: ");
                    String ISBN = scanner.nextLine();
                    libraryBookManagement.modifyBook(ISBN);
                    System.out.println("Do you want to modify another one?");
                    modifyFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.println("Enter the title of the book: ");
                    String title = scanner.nextLine();
                    libraryBookManagement.searchBooksByTitle(title);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to modify?");
                        int userTitleOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.modifyBook(this.bookISBNByOrder(userTitleOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    }
                case "3":
                    System.out.println("Enter the author of the book: ");
                    String author = scanner.nextLine();
                    libraryBookManagement.searchBooksByTitle(author);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to modify?");
                        int userAuthorOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.modifyBook(this.bookISBNByOrder(userAuthorOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    }
                case "4":
                    System.out.print("Enter the category of the book: ");
                    String category = scanner.nextLine();
                    libraryBookManagement.searchBooksByCategory(category);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to modify?");
                        int userCategoryOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.modifyBook(this.bookISBNByOrder(userCategoryOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    }
                default:
                    System.out.println("Invalid input. Try again (y) or press 0 to back to main menu");
                    String userChoice = scanner.nextLine();
                    if (userChoice.equals("0")) {
                        modifyFlag = false;
                    } else {
                        modifyFlag = modifyFlag;
                    }
                    break;
            }
        }
    }

    //Các phương thức cập nhật số lượng bản copy cho sách
    public void addCopiesToBook() {
        boolean addCopiesFlag = true;
        while (addCopiesFlag) {
            System.out.println("Which search criteria do you want to choose?");
            System.out.println("1. Search Books By ISBN");
            System.out.println("2. Search Books By Title");
            System.out.println("3. Search Books By Author");
            System.out.println("4. Search Books By Category");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    System.out.println("Enter the ISBN of the book: ");
                    String ISBN = scanner.nextLine();
                    libraryBookManagement.addCopiesWithISBN(ISBN);
                    System.out.println("Do you want to add copies for another one?");
                    addCopiesFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.println("Enter the title of the book: ");
                    String title = scanner.nextLine();
                    libraryBookManagement.searchBooksByTitle(title);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to add copies for another one?");
                        addCopiesFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to add copies?");
                        int userTitleOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.addCopiesWithISBN(this.bookISBNByOrder(userTitleOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to add copies for another one?");
                        addCopiesFlag = this.tryAgain();
                        break;
                    }
                case "3":
                    System.out.println("Enter the author of the book: ");
                    String author = scanner.nextLine();
                    libraryBookManagement.searchBooksByAuthor(author);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to add copies for another one?");
                        addCopiesFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to add copies?");
                        int userAuthorOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.addCopiesWithISBN(this.bookISBNByOrder(userAuthorOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to add copies for another one?");
                        addCopiesFlag = this.tryAgain();
                        break;
                    }
                case "4":
                    System.out.print("Enter the category of the book: ");
                    String category = scanner.nextLine();
                    libraryBookManagement.searchBooksByCategory(category);
                    if (libraryBookManagement.getBookMap().isEmpty()) {
                        System.out.println("Do you want to add copies for another one?");
                        addCopiesFlag = this.tryAgain();
                        break;
                    } else {
                        System.out.println("Choose the order's book which you want to add copies?");
                        int userCategoryOrder = Integer.parseInt(scanner.nextLine());
                        libraryBookManagement.addCopiesWithISBN(this.bookISBNByOrder(userCategoryOrder));
                        libraryBookManagement.setBookMap(new HashMap<>());
                        System.out.println("Do you want to add copies for another one?");
                        addCopiesFlag = this.tryAgain();
                        break;
                    }
                default:
                    System.out.println("Invalid input. Try again (y) or press 0 to back to main menu");
                    String userChoice = scanner.nextLine();
                    if (userChoice.equals("0")) {
                        addCopiesFlag = false;
                    } else {
                        addCopiesFlag = true;
                    }
                    break;
            }
        }
    }

    public void eventHandler() {
        boolean flag = true;
        while (flag) {
            displayMenu();
            int choice;
            choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 0:
                    System.out.println("See you next time.");
                    flag = false;
                    break;
                case 1:
                    libraryBookManagement.addBooks();
                    break;
                case 2:
                    removeBookByCriteria();
                    break;
                case 3:
                    searchByCriteria();
                    break;
                case 4:
                    boolean flag4 = true;
                    while (flag4) {
                        System.out.println("Which option do you want to update? >>");
                        System.out.println("1. Modify Features Of Book.\n2. Add Copies To Book.\n3. Back");
                        switch (scanner.nextLine()) {
                            case "1":
                                modifyBookByCriteria();
                                break;
                            case "2":
                                addCopiesToBook();
                                break;
                            case "3":
                                flag4 = false;
                                break;
                            default:
                                System.out.println("Invalid input. Try again (y) or press 0 to back to main menu");
                                String userChoice = scanner.nextLine();
                                if (userChoice.equals("0")) {
                                    flag4 = false;
                                } else {
                                    flag4 = true;
                                }
                                break;
                        }
                    }
                    break;
                case 5:
                    displayAllBooks();
                    break;
                default:
                    System.out.println("Option is invalid. Return to main menu.");
                    break;
            }
        }
    }
}
