package library.database.dao.impl;

import library.database.DatabaseConnection;
import library.models.Magazine;
import library.models.enums.LibraryItemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class MagazineDAOImpl {
    private static final Logger logger = LoggerFactory.getLogger(MagazineDAOImpl.class);

    private static final String INSERT_MAGAZINE = """
        INSERT INTO magazine (item_id, issue_number, publisher, category)
        VALUES (?, ?, ?, ?)
    """;

    private static final String UPDATE_MAGAZINE = """
        UPDATE magazine 
        SET issue_number = ?, publisher = ?, category = ?
        WHERE item_id = ?
    """;

    private static final String FIND_MAGAZINE_BY_ID = """
        SELECT li.*, m.issue_number, m.publisher, m.category
        FROM library_item li
        JOIN magazine m ON li.id = m.item_id
        WHERE li.id = ?
    """;

    public void save(int itemId, Magazine magazine, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_MAGAZINE)) {
            preparedStatement.setInt(1, itemId);
            preparedStatement.setString(2, magazine.getIssueNumber());
            preparedStatement.setString(3, magazine.getPublisher());
            preparedStatement.setString(4, magazine.getCategory());
            preparedStatement.executeUpdate();
        }
    }

    public void update(Magazine magazine, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_MAGAZINE)) {
            preparedStatement.setString(1, magazine.getIssueNumber());
            preparedStatement.setString(2, magazine.getPublisher());
            preparedStatement.setString(3, magazine.getCategory());
            preparedStatement.setInt(4, magazine.getId());
            preparedStatement.executeUpdate();
        }
    }

    public Magazine findById(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(FIND_MAGAZINE_BY_ID);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return extractMagazineFromResultSet(rs);
            }

            return null;

        } finally {
            if (rs != null) rs.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    private Magazine extractMagazineFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        LocalDate publishDate = resultSet.getDate("publish_date").toLocalDate();
        LibraryItemStatus status = LibraryItemStatus.valueOf(resultSet.getString("status"));
        String issueNumber = resultSet.getString("issue_number");
        String publisher = resultSet.getString("publisher");
        String category = resultSet.getString("category");
        Date returnDate = resultSet.getDate("return_date");
        LocalDate returnLocalDate = returnDate != null ? returnDate.toLocalDate() : null;

        return new Magazine(id, title, author, status, publishDate,
                issueNumber, publisher, category, returnLocalDate);
    }
}