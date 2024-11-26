package com.demo.zlib.Source;

import java.util.*;

public class LibraryBookManagement  {
    private Scanner input = new Scanner(System.in);
    private Library library = new Library();
    private Map<Book, Integer> bookMap = new HashMap<>();
    private final String header = String.format("%-10s %-15s %-70s %-25s %-35s %-35s %-10s %-10s %-10s%n",
            "Order", "ISBN", "Title", "Author", "Publisher", "Category", "Year", "Copies", "Status")
            + "---------------------------------------------------------------------------------------------------" +
            "-------------------------------------------------------";

    public String getHeader() {
        return header;
    }

    //Map setter
    public void setBookMap(Map<Book, Integer> bookMap) {
        this.bookMap = bookMap;
    }

    //Map getter
    public Map<Book, Integer> getBookMap() {
        return bookMap;
    }

    // 1. Các phương thức liên quan đến sách

    // Tạo một quyển sách
    public Book createBook() {
        System.out.println("Please enter the entire of book information: ");
        System.out.print("Book ISBN: ");
        String bookISBN = input.nextLine();
        if (isExistingISBN(bookISBN)) {
            System.out.println("Error: Book with this ISBN already exists!");
            return null;
        }
        System.out.print("Book title: ");
        String bookTitle = input.nextLine();
        System.out.print("Book author: ");
        String bookAuthor = input.nextLine();
        System.out.print("Book publisher: ");
        String bookPublisher = input.nextLine();
        System.out.print("Book category: ");
        String bookCategory = input.nextLine();
        System.out.print("Book year: ");
        String bookYear = input.nextLine();
        Book book = new Book(bookTitle, bookAuthor, bookPublisher, bookISBN,
                bookCategory,0, "Not Available", bookYear);
        return book;
    }

    // Thêm sách
    public void addBooks() {
        boolean flag = true;
        while (flag) {
            Book book = createBook();
            library.addBook(book);
            System.out.println("Do you want to add another book to the library? (y/n)");
            String answer = input.nextLine();
            if (answer.equals("y")) {
                flag = flag;
            } else {
                flag = !flag;
            }
        }
    }

    // Xóa sách
    public void removeBooksByISBN(String ISBN) {
        Book book = bookWithISBN(ISBN);
        if (book != null) {
            library.removeBook(book);
        } else {
            System.out.println("No book found with the given ISBN.");
        }
    }

    // Tìm kiếm sách theo ISBN
    public String findISBN() {
        System.out.print("Enter the ISBN of the book you want: ");
        String bookISBN = input.nextLine();
        return bookISBN;
    }

    // Kiểm tra sách đã tồn tại chưa
    public boolean isExistingISBN(String ISBN) {
        return (bookWithISBN(ISBN) != null);
    }

    // Trả về quyển sách với mã ISBN
    public Book bookWithISBN(String ISBN) {
        for (Book book : library.books) {
            if (book.getISBN().equals(ISBN)) {
                return book;
            }
        }
        return null;
    }

    //Trả về sách có thứ tự i
    public Book getBookByOrder(Map<Book, Integer> bookMap, int order) {
        for (Map.Entry<Book, Integer> entry : bookMap.entrySet()) {
            if (entry.getValue() == order) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Hiển thị tất cả các sách trong thư viện
    public void displayAllBooks() {
        if (library.getBooks().isEmpty()) {
            System.out.println("There are no books in the library.");
        } else {
            System.out.println("There are " + library.getBooks().size() + " books in the library: ");
            System.out.println(header);
            int i = 0;
            for (Book book : library.getBooks()) {
                System.out.print(String.format("%-10s ",++i));
                System.out.print(book.toString());
            }
        }
    }

    // Tìm kiếm sách theo tiêu chí
    // Hiển thị danh sách sách theo ISBN
    public void searchBooksByISBN(String ISBN) {
        Book book = bookWithISBN(ISBN);
        if (book != null) {
            System.out.println(header);
            String order = String.format("%-10s",1);
            System.out.print(order + " " + book.toString());
        } else {
            System.out.println("There is no book with this ISBN !");
        }
    }

    public void searchBooksByTitle(String title) {
        List<Book> result = new ArrayList<>();
        for (Book book : library.books) {
            if (book.getTitle().toLowerCase().contains(title.toLowerCase())) {
                result.add(book);
            }
        }
        if (result.size() > 0) {
            int i = 1;
            System.out.println("There are " + result.size() + " books with the title " + title + ":");
            System.out.println(header);
            for (Book book : result) {
                String order = String.format("%-10s ",i);
                System.out.print(order);
                System.out.print(book.toString());
                bookMap.put(book, i);
                i++;
            }
        } else {
            System.out.println("There is no book with the title " + title + ".");
        }
    }

    public void searchBooksByAuthor(String author) {
        List<Book> result = new ArrayList<>();
        for (Book book : library.books) {
            if (book.getAuthor().toLowerCase().contains(author.toLowerCase())) {
                result.add(book);
            }
        }
        if (result.size() > 0) {
            int i = 1;
            System.out.println("There are " + result.size() + " books with the author " + author + ":");
            System.out.println(header);
            for (Book book : result) {
                String order = String.format("%-10s ",i);
                System.out.print(order);
                System.out.print(book.toString());
                bookMap.put(book, i);
                i++;
            }
        } else {
            System.out.println("There is no book with the author " + author + ".");
        }
    }

    public void searchBooksByCategory(String category) {
        List<Book> result = new ArrayList<>();
        for (Book book : library.books) {
            if (book.getCategory().toLowerCase().contains(category.toLowerCase())) {
                result.add(book);
            }
        }
        if (result.size() > 0) {
            int i = 1;
            System.out.println("There are " + result.size() + " books with the category " + category + ":");
            System.out.println(header);
            for (Book book : result) {
                String order = String.format("%-10s ",i);
                System.out.print(order);
                System.out.print(book.toString());
                bookMap.put(book, i);
                i++;
            }
        } else {
            System.out.println("There is no book with the category " + category + ".");
        }
    }

    // Cập nhật thông tin sách
    public void modifyBook(String bookISBN) {
        Book bookToModify = bookWithISBN(bookISBN);
        if (bookToModify != null) {
            System.out.println(header);
            String order = String.format("%-10s ",1);
            System.out.print(order);
            System.out.println(bookToModify.toString());
            System.out.println("Please fill new features for this book");
            System.out.print("Enter ISBN >> ");
            String ISBN = input.nextLine();
            System.out.print("Enter title >> ");
            String newTitle = input.nextLine();
            System.out.print("Enter author >> ");
            String newAuthor = input.nextLine();
            System.out.print("Enter publisher >> ");
            String newPublisher = input.nextLine();
            System.out.print("Enter category >> ");
            String newCategory = input.nextLine();
            System.out.print("Enter year >> ");
            String newYear = input.nextLine();

            library.updateBookField(bookISBN, "Title", newTitle);
            bookToModify.setTitle(newTitle);
            library.updateBookField(bookISBN, "Author", newAuthor);
            bookToModify.setAuthor(newAuthor);
            library.updateBookField(bookISBN, "Publisher", newPublisher);
            bookToModify.setPublisher(newPublisher);
            library.updateBookField(bookISBN, "Category", newCategory);
            bookToModify.setCategory(newCategory);
            library.updateBookField(bookISBN, "Year", newYear);
            bookToModify.setYear(newYear);
            if (!Objects.equals(bookISBN, ISBN)) {
                library.updateBookField(bookISBN, "ISBN", ISBN);
                bookToModify.setISBN(ISBN);
            }
            System.out.println("This book has just updated! New book is:\n");
            System.out.println(header);
            System.out.print(order);
            System.out.println(bookToModify.toString());
        } else {
            System.out.println("There is no book with this ISBN !");
        }
    }

    // 3. Các phương thức tiện ích bổ sung

    // Kiểm tra trạng thái sách
    public void checkBookStatus() {
        String bookISBN = findISBN();
        Book book = bookWithISBN(bookISBN);
        if (book != null) {
            for (BookCopy copy : book.getCopies()) {
                System.out.println("Copy ID: " + copy.getCopyID() + ", Status: " + copy.getStatus());
            }
        } else {
            System.out.println("Book with ISBN " + bookISBN + " not found.");
        }
    }

    //Thêm số lượng copy cho sách
    public void addCopiesWithISBN(String ISBN) {
        Book book = bookWithISBN(ISBN);
        if (book != null) {
            System.out.print("Enter the available number of copy >> ");
            int quantity = Integer.parseInt(input.nextLine());
            library.addCopies(book, quantity);
            if (book.getCopies() != null) {
                book.setStatus("Available");
                library.updateBookField(ISBN, "Status", "Available");
            }
            library.updateBookField(ISBN, "copiesQuantity", quantity);
        } else {
            System.out.println("There is no book with this ISBN !");
        }
    }

    //Chưa phát triển
    // Cập nhật số lượng bản copy
    public void modifyCopies(int quantity, String modify) {
        String bookISBN = findISBN();
        Book bookToModify = bookWithISBN(bookISBN);
        if (bookToModify != null) {
            if (modify.equals("Stop supplying")) {
                for (int i = 0; i < quantity; i++) {
                    System.out.print("Enter the copyID of the book's copy you want to remove >> ");
                    int copyID = input.nextInt();
                    BookCopy copy = bookToModify.getCopyByID(copyID);
                    bookToModify.removeCopy(copy);
                }
            } else if (modify.equals("Additional")) {
                library.addCopies(bookToModify, quantity);
            }
        }
    }
}
