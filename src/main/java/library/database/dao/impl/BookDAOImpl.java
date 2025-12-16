package library.database.dao.impl;

import library.database.DatabaseConnection;
import library.models.Book;
import library.models.enums.LibraryItemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class BookDAOImpl {
    private static final Logger logger = LoggerFactory.getLogger(BookDAOImpl.class);

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

    public void save(int itemId, Book book, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_BOOK)) {
            preparedStatement.setInt(1, itemId);
            preparedStatement.setString(2, book.getIsbn());
            preparedStatement.setString(3, book.getGenre());
            preparedStatement.setInt(4, book.getPageCount());
            preparedStatement.executeUpdate();
        }
    }

    public void update(Book book, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_BOOK)) {
            preparedStatement.setString(1, book.getIsbn());
            preparedStatement.setString(2, book.getGenre());
            preparedStatement.setInt(3, book.getPageCount());
            preparedStatement.setInt(4, book.getId());
            preparedStatement.executeUpdate();
        }
    }

    public Book findById(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(FIND_BOOK_BY_ID);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return extractBookFromResultSet(rs);
            }

            return null;

        } finally {
            if (rs != null) rs.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
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