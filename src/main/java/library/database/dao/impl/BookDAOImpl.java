package library.database.dao.impl;

import library.database.dao.LibraryItemDetailDAO;
import library.database.util.DBUtil;
import library.models.Book;
import library.models.enums.LibraryItemStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class BookDAOImpl extends BaseDAO implements LibraryItemDetailDAO<Book> {
    private static final String INSERT_BOOK = """
        INSERT INTO book (item_id, isbn, genre, page_count)
        VALUES (?, ?, ?, ?)
    """;

    private static final String UPDATE_BOOK = """
        UPDATE book 
        SET isbn = ?, genre = ?, page_count = ?
        WHERE item_id = ?
    """;

    private static final String FIND_BOOK_BY_ID = """
        SELECT li.*, b.isbn, b.genre, b.page_count
        FROM library_item li
        JOIN book b ON li.id = b.item_id
        WHERE li.id = ?
    """;

    @Override
    public void save(int itemId, Book book, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, INSERT_BOOK,
                itemId, book.getIsbn(), book.getGenre(), book.getPageCount());
    }

    @Override
    public void update(Book book, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, UPDATE_BOOK,
                book.getIsbn(), book.getGenre(), book.getPageCount(), book.getId());
    }

    @Override
    public Book findById(int id) throws SQLException {
        return DBUtil.executeQueryAndMap(FIND_BOOK_BY_ID, this::extractBookFromResultSet, id);
    }

    private Book extractBookFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        LocalDate publishDate = resultSet.getDate("publish_date").toLocalDate();
        LibraryItemStatus status = LibraryItemStatus.valueOf(resultSet.getString("status"));
        String isbn = resultSet.getString("isbn");
        String genre = resultSet.getString("genre");
        int pageCount = resultSet.getInt("page_count");
        Date returnDate = resultSet.getDate("return_date");
        LocalDate returnLocalDate = returnDate != null ? returnDate.toLocalDate() : null;

        return new Book(id, title, pageCount, author, status, publishDate, isbn, genre, returnLocalDate);
    }
}