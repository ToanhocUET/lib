package com.demo.zlib.Source;

import java.util.HashMap;
import java.util.Objects;
import java.util.Scanner;

public class CommandLineInterfaceMember {
    private Scanner scanner = new Scanner(System.in);
    private LibraryMemberManagement libraryMemberManagement = new LibraryMemberManagement();

    public void displayMenu() {
        System.out.println("Welcome to the library management interface for members.");
        System.out.println("-----------------------------------------");
        System.out.println("[0] Exit");
        System.out.println("[1] Add Member");//
        System.out.println("[2] Remove Member");//
        System.out.println("[3] Search Member");
        System.out.println("[4] Update Member");//
        System.out.println("[5] Display Member");
        System.out.println("------------------------------------------");
    }

    //Phương thức hiển thị tất cả thành viên
    public void displayAllMembers() {
        boolean displayFlag = true;
        while (displayFlag) {
            libraryMemberManagement.displayAllMembers();
            while (true) {
                System.out.println("Press 0 to back to main menu.");
                int userChoice = scanner.nextInt();
                scanner.nextLine();
                if (userChoice == 0) {
                    displayFlag = false;
                    break;
                } else {
                    System.out.println("Invalid input. Try again.");
                }
            }
        }
    }
    //Phương thức tìm kiếm thành viên theo tiêu chí
    public boolean tryAgain() {
        System.out.println("y : Yes");
        System.out.println("n : No. Back to main menu");
        String answer = scanner.nextLine();
        if (answer.equals("y")) {
            return true;
        } else {
            return false;
        }
    }

    public void searchMembersByCriteria() {
        boolean searchFlag = true;
        while (searchFlag) {
            System.out.println("Which criteria would you like to search?");
            System.out.println("[1] Search By ID");
            System.out.println("[2] Search By Name");
            System.out.println("[3] Search By Contact");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    System.out.print("Enter the ID of the member >> ");
                    int id;
                    while (true) {
                        String input = scanner.nextLine();
                        try {
                            id = Integer.parseInt(input);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Invalid input. Try again.");
                            scanner.nextLine();
                        }
                    }
                    libraryMemberManagement.searchMembersByID(id);
                    libraryMemberManagement.setMemberMap(new HashMap<>());
                    System.out.println("Do you want to search another one?");
                    searchFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.print("Enter the name of the member >> ");
                    String name = scanner.nextLine();
                    libraryMemberManagement.searchMembersByName(name);
                    libraryMemberManagement.setMemberMap(new HashMap<>());
                    System.out.println("Do you want to search another one?");
                    searchFlag = this.tryAgain();
                    break;
                case "3":
                    System.out.print("Enter the contact of the member >> ");
                    String contact = scanner.nextLine();
                    libraryMemberManagement.searchMembersByContact(contact);
                    libraryMemberManagement.setMemberMap(new HashMap<>());
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

    //Phương thức xóa thành viên theo tiêu chí
    //Phương thức lấy ID theo số thứ tự
    public int getIDByOrder(int order) {
        Member member = libraryMemberManagement.getMemberByOrder(libraryMemberManagement.getMemberMap(), order);
        return member.getMemberID();
    }

    //Phương thức xử lí ngoại lệ khi chọn thứ tự thành viên
    public void removeMemberHandler(String criteria) {
        if (Objects.equals(criteria, "ID")) {

            System.out.println("Enter the ID of the member you want to remove: ");
            int ID;
            while (true) {
                String input = scanner.nextLine();
                try {
                    ID = Integer.parseInt(input);
                    libraryMemberManagement.removeMemberByID(ID);
                    break;
                } catch (NumberFormatException e) {
                    System.out.println("Invalid input. Try again.");
                    scanner.nextLine();
                }
            }
        } else {
            System.out.println("Which member do you want to remove?");
            int userMemberOrder;
            while (true) {
                System.out.print("Order: ");
                String input = scanner.nextLine();
                try {
                    userMemberOrder = Integer.parseInt(input);
                    libraryMemberManagement.removeMemberByID(this.getIDByOrder(userMemberOrder));
                    libraryMemberManagement.setMemberMap(new HashMap<>());
                    break;
                } catch (NumberFormatException | NullPointerException e) {
                    System.out.println("Invalid input. Please enter an valid order.");
                }
            }
        }
    }

    //Xóa thành viên theo tiêu chí
    public void removeMembersByCriteria() {
        boolean removeFlag = true;
        while (removeFlag) {
            System.out.println("Which criteria would you like to remove?");
            System.out.println("[1] Remove By ID");
            System.out.println("[2] Remove By Name");
            System.out.println("[3] Remove By Contact");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    removeMemberHandler("ID");
                    System.out.println("Do you want to remove another one?");
                    removeFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.print("Enter the name of the member you want to remove: ");
                    String userName = scanner.nextLine();
                    libraryMemberManagement.searchMembersByName(userName);
                    if (libraryMemberManagement.getMemberMap().isEmpty()) {
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    } else {
                        removeMemberHandler("Name");
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    }
                case "3":
                    System.out.print("Enter the contact of the member you want to remove: ");
                    String userContact = scanner.nextLine();
                    libraryMemberManagement.searchMembersByName(userContact);
                    if (libraryMemberManagement.getMemberMap().isEmpty()) {
                        System.out.println("Do you want to remove another one?");
                        removeFlag = this.tryAgain();
                        break;
                    } else {
                        removeMemberHandler("Contact");
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

    //Các phương thức cập nhật thuộc tính cho member
    //Xử lí lựa chọn thứ tự thành viên
    public void modifyMemberHandler() {
        System.out.println("Which member do you want to modify?");
        int userMemberOrder;
        while (true) {
            System.out.print("Order: ");
            String input = scanner.nextLine();
            try {
                userMemberOrder = Integer.parseInt(input);
                libraryMemberManagement.modifyMember(this.getIDByOrder(userMemberOrder));
                libraryMemberManagement.setMemberMap(new HashMap<>());
                break;
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println("Invalid input. Please enter an valid order.");
            }
        }
    }

    public void modifyMemberByCriteria() {
        boolean modifyFlag = true;
        while (modifyFlag) {
            System.out.println("Which search criteria would you like to choose?");
            System.out.println("[1] Search By ID");
            System.out.println("[2] Search By Name");
            System.out.println("[3] Search By Contact");
            String type = scanner.nextLine();
            switch (type) {
                case "1":
                    System.out.print("Enter the ID of the member that need to modify >> ");
                    int ID;
                    while (true) {
                        String input = scanner.nextLine();
                        try {
                            ID = Integer.parseInt(input);
                            break;
                        } catch (NumberFormatException e) {
                            System.out.println("Please enter an integer ID.");
                            scanner.nextLine();
                        }
                    }
                    libraryMemberManagement.modifyMember(ID);
                    System.out.println("Do you want to modify another one?");
                    modifyFlag = this.tryAgain();
                    break;
                case "2":
                    System.out.print("Enter the name of the member that need to modify >> ");
                    String name = scanner.nextLine();
                    libraryMemberManagement.searchMembersByName(name);
                    if (libraryMemberManagement.getMemberMap().isEmpty()) {
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    } else {
                        modifyMemberHandler();
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    }
                case "3":
                    System.out.print("Enter the contact of the member that need to modify >> ");
                    String contact = scanner.nextLine();
                    libraryMemberManagement.searchMembersByContact(contact);
                    if (libraryMemberManagement.getMemberMap().isEmpty()) {
                        System.out.println("Do you want to modify another one?");
                        modifyFlag = this.tryAgain();
                        break;
                    } else {
                        modifyMemberHandler();
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
                        modifyFlag = true;
                    }
                    break;
            }
        }
    }

    //Xử lí lựa chọn
    public void eventsHandler() {
        boolean flag = true;
        while (flag) {
            displayMenu();
            System.out.print("Choose an option above >> ");
            int choice = scanner.nextInt();
            scanner.nextLine();
            switch (choice) {
                case 0:
                    System.out.println("See you around!");
                    flag = false;
                    break;
                case 1:
                    libraryMemberManagement.addMembers();
                    break;
                case 2:
                    removeMembersByCriteria();
                    break;
                case 3:
                    searchMembersByCriteria();
                    break;
                case 4:
                    modifyMemberByCriteria();
                    break;
                case 5:
                    displayAllMembers();
                    break;
                default:
                    while (true) {
                        System.out.println("Invalid input. Press 0 to back to main menu.");
                        int choice2 = scanner.nextInt();
                        scanner.nextLine();
                        if (choice2 == 0) {
                            break;
                        }
                    }
                    break;
            }
        }
    }
}
