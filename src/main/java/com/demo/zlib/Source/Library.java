package com.demo.zlib.Source;

import java.util.ArrayList;
import java.util.List;
import java.sql.*;

public class Library {
    private static final String name = "ZLibrary";
    private static final String address = "144 Xuan Thuy, Cau Giay, Ha Noi, Viet Nam";
    private static final String dataPath = "Books.db";
    private static final String memberPath = "Members.db";
    private static final String copiesPath = "BooksCopies.db";
    protected List<Book> books;
    private List<BookCopy> bookCopies;
    protected List<Member> members;

    public Library() {
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.bookCopies = new ArrayList<>();
        loadBooksFromDatabase();
        loadMembersFromDatabase();
        loadCopiesFromDatabase();
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public List<Book> getBooks() {
        return books;
    }

    public List<Member> getMembers() {
        return members;
    }

    public List<BookCopy> getBookCopies() {
        return bookCopies;
    }

    public void setBookCopies(List<BookCopy> bookCopies) {
        this.bookCopies = bookCopies;
    }

    public void loadBooksFromDatabase() {
        String url = null;
        try {
            url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(dataPath).getPath();
        } catch (NullPointerException e) {
            System.out.println("Không tìm thấy file database trong thư mục resources.");
            return;
        }

        String sql = "SELECT * FROM Books"; // Giả sử bảng sách có tên là 'books'

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ISBN = rs.getString("ISBN");
                String title = rs.getString("Title");
                String author = rs.getString("Author");
                String publisher = rs.getString("Publisher");
                String category = rs.getString("Category");
                String year = rs.getString("Year");
                int copies = rs.getInt("copiesQuantity");
                String status = rs.getString("Status");

                Book book = new Book(title, author, publisher, ISBN, category,
                        copies, status, year);
                this.books.add(book);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tải sách từ cơ sở dữ liệu: " + e.getMessage());
        }
    }

    //Add 1 quyển vào database
    public void insertBook(Book book) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(dataPath).getPath(); // Đường dẫn tới file Books.db

        // Câu lệnh SQL để chèn một sách vào bảng Books
        String sql = "INSERT INTO Books (ISBN, title, author, publisher, category, year, copiesQuantity, status) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập các giá trị cho câu lệnh INSERT
            pstmt.setString(1, book.getISBN());
            pstmt.setString(2, book.getTitle());
            pstmt.setString(3, book.getAuthor());
            pstmt.setString(4, book.getPublisher());
            pstmt.setString(5, book.getCategory());
            pstmt.setString(6, book.getYear());
            pstmt.setInt(7, book.getCopiesQuantity());
            pstmt.setString(8, book.getStatus());

            // Thực hiện câu lệnh INSERT
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Lỗi khi chèn sách vào cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức để xóa một quyển sách khỏi cơ sở dữ liệu theo ISBN
    public void deleteBookByISBN(String isbn) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(dataPath).getPath(); // Đường dẫn tới file Books.db

        // Câu lệnh SQL để xóa một sách khỏi bảng Books theo ISBN
        String sql = "DELETE FROM Books WHERE ISBN = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập giá trị cho câu lệnh DELETE
            pstmt.setString(1, isbn);

            // Thực hiện câu lệnh DELETE
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Lỗi khi xóa sách khỏi cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Phương thức để cập nhật một thuộc tính cụ thể của sách trong cơ sở dữ liệu
    public void updateBookField(String isbn, String fieldName, Object newValue) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(dataPath).getPath(); // Đường dẫn tới file Books.db

        // Câu lệnh SQL để cập nhật một trường cụ thể của sách trong bảng Books theo ISBN
        String sql = "UPDATE Books SET " + fieldName + " = ? WHERE ISBN = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập giá trị mới cho câu lệnh UPDATE
            pstmt.setObject(1, newValue);
            pstmt.setString(2, isbn);

            // Thực hiện câu lệnh UPDATE
            pstmt.executeUpdate();

        } catch (SQLException e) {
            System.out.println("Lỗi khi cập nhật " + fieldName + " trong cơ sở dữ liệu: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //tải thành viên từ database
    public void loadMembersFromDatabase() {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(memberPath).getPath();
        String sql = "SELECT * FROM Members";
        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                //Tạo đối tượng Member từ database
                int memberID = rs.getInt("memberID");
                String name = rs.getString("Name");
                String contactInfo = rs.getString("Contact");

                Member member = new Member(memberID, name, contactInfo);
                this.members.add(member);
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tải từ database " + e.getMessage());
        }
    }

    //Add một member vào database
    public void insertMember(Member member) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(memberPath).getPath();
        String sql = "INSERT INTO Members (memberID, Name, Contact) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            //Thiết lập các giá trị
            pstm.setInt(1, member.getMemberID());
            pstm.setString(2, member.getName());
            pstm.setString(3, member.getContactInfo());

            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Loi " + e.getMessage());
        }
    }

    //Xoa mot member khoi database
    public void deleteMemberByID(int memberID) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(memberPath).getPath();
        String sql = "DELETE FROM Members WHERE MemberID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, memberID);

            //Thuc hien cau lenh delete
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Loi " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Cap nhat thuoc tinh cho thanh vien
    public void updateMemberField(int memberID, String fieldName, Object newValue) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(memberPath).getPath();
        String sql = "UPDATE Members SET " + fieldName + " = ? WHERE MemberID = ?";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            //thiet lap gia tri moi
            pstmt.setObject(1, newValue);
            pstmt.setInt(2, memberID);

            //Thuc hien cau lenh update
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Loi " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Add một member vào database
    public void insertCopy(BookCopy bookCopy) {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(copiesPath).getPath();
        String sql = "INSERT INTO BooksCopies (ISBN, copyID, status) VALUES (?, ?, ?)";

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstm = conn.prepareStatement(sql)) {
            //Thiết lập các giá trị
            pstm.setString(1, bookCopy.getBook().getISBN());
            pstm.setInt(2, bookCopy.getCopyID());
            pstm.setString(3, bookCopy.getStatus());
            pstm.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Loi " + e.getMessage());
        }
    }

    public void loadCopiesFromDatabase() {
        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(copiesPath).getPath();
        String sql = "SELECT * FROM BooksCopies";

        try (Connection conn = DriverManager.getConnection(url);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                String ISBN = rs.getString("ISBN");
                int copyID = rs.getInt("CopyID");
                String status = rs.getString("Status");

                // Tìm sách tương ứng với ISBN
                Book book = findBookByISBN(ISBN);
                if (book != null) {
                    // Tạo bản copy mới và gắn nó vào sách tương ứng
                    BookCopy copy = new BookCopy(book);
                    copy.setCopyID(copyID);
                    copy.setStatus(status);

                    // Thêm bản copy vào danh sách của sách
                    book.addNewCopy(copy);

                    // Thêm bản copy vào danh sách tổng
                    this.bookCopies.add(copy);
                }
            }
        } catch (SQLException e) {
            System.out.println("Lỗi khi tải từ database " + e.getMessage());
        }
    }

    // Phương thức phụ để tìm sách dựa trên ISBN
    public Book findBookByISBN(String ISBN) {
        for (Book book : books) {
            if (book.getISBN().equals(ISBN)) {
                return book;
            }
        }
        return null;
    }

    public void updateBookCopyField(int copyID, String fieldName, Object newValue) {
        String sql = "UPDATE BooksCopies SET " + fieldName + " = ? WHERE copyID = ?";

        String url = "jdbc:sqlite:" + getClass().getClassLoader().getResource(copiesPath).getPath();

        try (Connection conn = DriverManager.getConnection(url);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {

            // Thiết lập giá trị cho câu lệnh UPDATE
            pstmt.setObject(1, newValue);
            pstmt.setInt(2, copyID);

            // Thực hiện câu lệnh UPDATE
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error updating " + fieldName + " in BooksCopies: " + e.getMessage());
            e.printStackTrace();
        }
    }

    //Thêm sách
    public void addBook(Book book) {
        if (book != null) {
            this.books.add(book);
            this.insertBook(book);
            System.out.println("Book added.");
        } else {
            System.out.println("Action failed.");
        }

    }

    //Thêm số lượng bản copy có sẵn cho sách
    public void addCopies(Book book, int copies) {
        for (int i = 0; i < copies; i++) {
            BookCopy copy = new BookCopy(book);
            book.addNewCopy(copy);
            this.insertCopy(copy);
        }
        book.setCopiesQuantity(book.getCopies().size());
        System.out.println("Book's copies have been added to library.");
    }

    //Xóa sách
    public void removeBook(Book book) {
        if (book != null) {
            this.books.remove(book);
            this.deleteBookByISBN(book.getISBN());
            System.out.println("Book removed.");
        } else {
            System.out.println("Action failed.");
        }
    }

    //Thêm thành viên
    public void addMember(Member member) {
        if (member != null) {
            this.members.add(member);
            this.insertMember(member);
            System.out.println("Member added.");
        } else {
            System.out.println("Action failed.");
        }
    }
    public void updateBook(Book updatedBook) {
        for (int i = 0; i < books.size(); i++) {
            if (books.get(i).getISBN().equals(updatedBook.getISBN())) {
                books.set(i, updatedBook);
                updateBookField(updatedBook.getISBN(), "Title", updatedBook.getTitle());
                updateBookField(updatedBook.getISBN(), "Author", updatedBook.getAuthor());
                updateBookField(updatedBook.getISBN(), "Publisher", updatedBook.getPublisher());
                updateBookField(updatedBook.getISBN(), "Category", updatedBook.getCategory());
                updateBookField(updatedBook.getISBN(), "Year", updatedBook.getYear());
                updateBookField(updatedBook.getISBN(), "CopiesQuantity", updatedBook.getCopiesQuantity());
                updateBookField(updatedBook.getISBN(), "Status", updatedBook.getStatus());
                return;
            }
        }
    }

    public Member findMemberByID(int memberID) {
        for (Member member : members) {
            if (member.getMemberID() == memberID) {
                return member;
            }
        }
        return null;
    }

    //Xóa thành viên
    public void removeMember(Member member) {
        this.members.remove(member);
        this.deleteMemberByID(member.getMemberID());
        System.out.println("Member removed : " + member.getName());
    }
    public void updateMember(Member updatedMember) {
        for (Member member : members) {
            if (member.getMemberID() == updatedMember.getMemberID()) {
                member.setName(updatedMember.getName());
                member.setContactInfo(updatedMember.getContactInfo());
            }
        }
    }
}