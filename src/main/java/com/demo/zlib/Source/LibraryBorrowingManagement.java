package com.demo.zlib.Source;

import java.sql.*;
import java.sql.Date;
import java.util.*;

public class LibraryBorrowingManagement {
    private Scanner scan = new Scanner(System.in);
    private List<Member> members = new ArrayList<>();
    private List<Book> books = new ArrayList<>();
    private List<BookCopy> bookCopies = new ArrayList<>();
    private List<Borrowing> borrowings = new ArrayList<>();
    private Library library;
    private LibraryMemberManagement libraryMemberManagement = new LibraryMemberManagement();
    private LibraryBookManagement libraryBookManagement = new LibraryBookManagement();
    private static final String borrowingPath = "Borrowings.db";
    private final String copyHeader = String.format("%-15s %-25s %-25s%n", "CopyID", "Status", "ISBN");
    private final String borrowingHeader = String.format("%-20s %-20s %-25s %-60s %-40s %-40s%n",
            "Borrowing ID", "copy ID", "ISBN", "Book", "Borrowing Date", "Due Date");

    public LibraryBorrowingManagement() {
        library = new Library();
        members = library.getMembers();
        books = library.getBooks();
        bookCopies = library.getBookCopies();
        loadBorrowingsFromDatabase();
    }

    private BookCopy findBookCopyByID(int copyID) {
        for (BookCopy bookCopy : bookCopies) {
            if (bookCopy.getCopyID() == copyID) {
                return bookCopy;
            }
        }
        return null;
    }

    public void loadBorrowingsFromDatabase() {
        String sql = "SELECT borrowingID, memberID, copyID, ISBN, BorrowDate, DueDate FROM Borrowings";
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(borrowingPath).getPath();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {

            borrowings.clear();

            while (rs.next()) {
                String borrowingID = rs.getString("borrowingID");
                int memberID = rs.getInt("memberID");
                int copyID = rs.getInt("copyID");
                String ISBN = rs.getString("ISBN");
                long borrowDateMillis = rs.getLong("BorrowDate");
                long dueDateMillis = rs.getLong("DueDate");

                Date borrowDate = new Date(borrowDateMillis);
                Date dueDate = new Date(dueDateMillis);

                Member member = libraryMemberManagement.getMemberByID(memberID);
                BookCopy bookCopy = findBookCopyByID(copyID); // Sử dụng copyID thay vì ISBN

                if (member != null && bookCopy != null) {
                    Borrowing borrowing = new Borrowing(borrowingID, member, borrowDate, dueDate, bookCopy);
                    borrowings.add(borrowing);
                } else {
                    System.out.println("Error: Inconsistent data for borrowing ID " + borrowingID);
                }
            }
        } catch (SQLException e) {
            System.out.println("Error loading borrowings from database: " + e.getMessage());
        }
    }



    public void addBorrowingToDatabase(Borrowing borrowing) {
        String sql = "INSERT INTO Borrowings (borrowingID, memberID, copyID, ISBN, BorrowDate, DueDate) VALUES (?, ?, ?, ?, ?, ?)";
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(borrowingPath).getPath();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập giá trị cho câu lệnh SQL
            pstmt.setString(1, borrowing.getBorrowingID());
            pstmt.setInt(2, borrowing.getMember().getMemberID());
            pstmt.setInt(3, borrowing.getCopyID());
            pstmt.setString(4, borrowing.getBookCopy().getBook().getISBN());
            pstmt.setLong(5, borrowing.getBorrowDate().getTime()); // Chuyển từ Date sang milliseconds
            pstmt.setLong(6, borrowing.getDueDate().getTime()); // Lưu ngày đáo hạn dưới dạng milliseconds

            // Thực hiện câu lệnh SQL
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error adding borrowing to the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức để xóa một Borrowing khỏi cơ sở dữ liệu
    public void deleteBorrowingFromDatabase(String borrowingID) {
        String sql = "DELETE FROM Borrowings WHERE borrowingID = ?";
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(borrowingPath).getPath();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập giá trị cho câu lệnh SQL
            pstmt.setString(1, borrowingID);

            // Thực hiện câu lệnh DELETE
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Error deleting borrowing from the database: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //
    public int issueExceptionHandler() {
        int id;
        while (true) {
            System.out.print("Enter ID >> ");
            String input = scan.nextLine();
            try {
                id = Integer.parseInt(input);
                break;
            } catch (NumberFormatException | NullPointerException e) {
                System.out.println("Invalid input. Please enter an valid id.");
            }
        }
        return id;
    }

    //Phương thức phát sách cho thành viên
    public void issueBook(Member member, Book book) {
        if (book.getStatus().equals("Available")) {
            System.out.println("All the copies of this book are listed below:");
            displayBookCopies(book);
            System.out.println("Which book's copy do you want to issue?");
            int copyID = issueExceptionHandler();
            for (BookCopy copy : bookCopies) {
                if (copy.getCopyID() == copyID) {
                    if (Objects.equals(copy.getStatus(), "Available")) {
                        copy.setStatus("Borrowed");
                        library.setBookCopies(bookCopies);
                        library.updateBookCopyField(copy.getCopyID(), "Status", "Borrowed");
                        Borrowing borrowing = new Borrowing(copy, member);
                        borrowings.add(borrowing);
                        addBorrowingToDatabase(borrowing);
                        System.out.println("You just issued a book to member " + member.getMemberID() + ".");
                        borrowing.displayBorrowingDetails();
                    } else {
                        System.out.println("This copy is already borrowed by other.");
                    }
                }
            }
        } else {
            System.out.println("This book is not available now. Please come back later");
        }
    }

    public void issueBooks() {
        while (true) {
            System.out.println("Enter the ID of the member that you want to issue: ");
            int memberId = issueExceptionHandler();
            Member member = libraryMemberManagement.getMemberByID(memberId);

            if (member == null) {
                if (!retryPrompt("Invalid member ID. Note that we require a correct member ID.")) {
                    System.out.println("Action failed. Cannot issue book to member.");
                    return;
                }
            } else {
                while (true) {
                    System.out.println("Enter the ISBN of the book you want to issue: ");
                    String ISBN = scan.nextLine();
                    Book book = null;
                    for (Book book1 : books) {
                        if (book1.getISBN().equals(ISBN)) {
                            book = book1;
                            break;
                        }
                    }
                    if (book == null) {
                        if (!retryPrompt("Invalid ISBN. Note that we require a correct ISBN.")) {
                            System.out.println("Action failed. Cannot issue book to member.");
                            return;
                        }
                    } else {
                        issueBook(member, book);
                        return;
                    }
                }
            }
        }
    }

    // Helper method to handle retry prompts
    private boolean retryPrompt(String errorMessage) {
        System.out.println(errorMessage);
        while (true) {
            System.out.print("Try again (y/n) >> ");
            String choice = scan.nextLine();
            if (choice.equalsIgnoreCase("y")) {
                return true;
            } else if (choice.equalsIgnoreCase("n")) {
                return false;
            } else {
                System.out.println("Invalid input. Please enter 'y' or 'n'.");
            }
        }
    }

    //Hiển thị trạng thái những bản cpoies của quyển sách
    public void displayBookCopies(Book book) {
        System.out.println(libraryBookManagement.getHeader());
        String order = String.format("%-10s ", 1);
        System.out.println(order + book.toString());
        System.out.println("This book has " + book.getCopies().size() + " copy:");
        if (book.getCopiesQuantity() > 0) {
            System.out.println(copyHeader);
            book.displayAllCopies();
        } else {
            System.out.println("No book copies available.");
        }
    }

    //Hiển thị trạng thái những bản copy theo tiêu chí
    public void displayCopiesByCriteria(String criteria) {
        bookCopies = library.getBookCopies();
        if (criteria.equals("ISBN")) {
            System.out.print("Enter the ISBN of the book you want to check its copies >> ");
            String ISBN = scan.nextLine();
            Book book = null;
            for (Book book1 : books) {
                if (book1.getISBN().equals(ISBN)) {
                    book = book1;
                    break;
                }
            }
            if (book == null) {
                System.out.println("There is no book with this ISBN.");
                return;
            } else {
                displayBookCopies(book);
            }
        } else if (criteria.equals("Title")) {
            System.out.print("Enter the Title of the book you want to check its copies >> ");
            String title = scan.nextLine();
            List<Book> bookWithTitle = new ArrayList<>();
            for (Book book : library.books) {
                if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                    bookWithTitle.add(book);
                }
            }
            if (!bookWithTitle.isEmpty()) {
                for (Book book : bookWithTitle) {
                    displayBookCopies(book);
                }
            } else {
                System.out.println("There is no book contains this title.");
            }
        }
    }
    public void returnBooks() {
        while (true) {
            System.out.print("Enter the borrowing ID that you want to return >> ");
            String borrowingID = scan.nextLine();

            // Tìm giao dịch mượn dựa trên ID
            Borrowing borrowingToReturn = null;
            for (Borrowing borrowing : borrowings) {
                if (borrowing.getBorrowingID().equals(borrowingID)) {
                    borrowingToReturn = borrowing;
                    break;
                }
            }

            if (borrowingToReturn == null) {
                // Nếu không tìm thấy giao dịch, hỏi người dùng có muốn thử lại không
                if (!retryPrompt("Error: Cannot find borrowing with ID: " + borrowingID + ". Do you want to try again?")) {
                    System.out.println("Action canceled. Returning book operation aborted.");
                    return; // Hủy thao tác nếu người dùng chọn "n"
                }
            } else {
                // Nếu tìm thấy giao dịch, thực hiện trả sách
                BookCopy bookCopy = borrowingToReturn.getBookCopy();
                for (BookCopy copy : bookCopies) {
                    if (bookCopy.getCopyID() == copy.getCopyID()) {
                        copy.setStatus("Available");
                    }
                }
                library.setBookCopies(bookCopies);
                library.updateBookCopyField(bookCopy.getCopyID(), "Status", "Available");

                // Xóa giao dịch mượn khỏi danh sách và cơ sở dữ liệu
                borrowings.remove(borrowingToReturn);
                deleteBorrowingFromDatabase(borrowingID);
                //load lại dữ liệu

                System.out.println("Book copy with ID " + bookCopy.getCopyID() + " has been returned successfully.");
                return; // Hoàn tất trả sách và thoát khỏi vòng lặp
            }
        }
    }


    public List<Borrowing> getBorrowingsByID(int id) {
        List<Borrowing> borrowingsByMember = new ArrayList<>();
        for (Borrowing borrowing : this.borrowings) {
            if (borrowing.getMember().getMemberID() == id) {
                borrowingsByMember.add(borrowing);
            }
        }
        return borrowingsByMember;
    }

    //Hiển thị tất cả giao dịch của thành viên
    public void displayMemberBorrowings(int memberId) {
        while (true) {
            Member member = libraryMemberManagement.getMemberByID(memberId);
            if (member == null) {
                if (!retryPrompt("Invalid member ID. Note that we require a correct member ID.")) {
                    System.out.println("Action failed. Cannot issue book to member.");
                    return;
                }
            } else {
                List<Borrowing> borrowingsMember = getBorrowingsByID(member.getMemberID());
                if (borrowingsMember.isEmpty()) {
                    System.out.println("Member " + member.getMemberID() + " does not borrow any book.");
                    return;
                } else {
                    System.out.println("Member " + member.getMemberID() + " has these following borrowings:");
                    System.out.println(borrowingHeader);
                    for (Borrowing borrowing : borrowingsMember) {
                        System.out.println(borrowing.toString());
                    }
                    return;
                }
            }
        }
    }

    public void eventHandler () {
        boolean flag = true;
        while (flag) {
            System.out.println("-----------------------------------");
            System.out.println("[0] Exit");
            System.out.println("[1] Display all copies");
            System.out.println("[2] Issue book");
            System.out.println("[3] Return book");
            System.out.println("[4] Display Member Borrowings");
            System.out.println("-----------------------------------");
            String choice = scan.nextLine();
            switch (choice) {
                case "0":
                    System.out.println("See you around.");
                    flag = false;
                    break;
                case "1":
                    while (true) {
                        System.out.println("Which option would you like to display?");
                        System.out.println("[1] Display all copies of all books");
                        System.out.println("[2] Display copies by book criteria");
                        String option = scan.nextLine();
                        switch (option) {
                            case "1":
                                for (Book book : library.books) {
                                    displayBookCopies(book);
                                }
                                break;
                            case "2":
                                System.out.println("Choose criteria:");
                                System.out.println("[1] Display copies by book ISBN");
                                System.out.println("[2] Display copies by book title");
                                String criteria = scan.nextLine();
                                switch (criteria) {
                                    case "1":
                                        displayCopiesByCriteria("ISBN");
                                        break;
                                    case "2":
                                        displayCopiesByCriteria("Title");
                                        break;
                                }
                                break;
                            default:
                                System.out.println("Invalid option.");
                                break;
                        }
                        System.out.println("Do you want to try agian? (y/n)");
                        String answer = scan.nextLine();
                        if (answer.equals("y")) {
                            System.out.println("Waiting...");
                        } else {
                            break;
                        }
                    }
                    break;
                case "2":
                    issueBooks();
                    break;
                case "3":
                    while (true) {
                        returnBooks();
                        System.out.println("Do you want to return another book? (y/n)");
                        String userChoice = scan.nextLine();
                        if (userChoice.equals("y")) {
                            System.out.println("Waiting...");
                        } else {
                            break;
                        }
                    }
                    break;
                case "4":
                    System.out.println("Enter the ID of the member that you want to check borrowings: ");
                    int memberId = issueExceptionHandler();
                    displayMemberBorrowings(memberId);
                    break;
                default:
                    System.out.println("Invalid option. Try again.");
                    break;
            }
        }
    }

    public List<Borrowing> getAllBorrowings() {
        return new ArrayList<>(borrowings); // Returning a copy of the list to prevent modification of the original
    }
}
