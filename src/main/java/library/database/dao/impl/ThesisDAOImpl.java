package library.database.dao.impl;

import library.database.DatabaseConnection;
import library.models.Thesis;
import library.models.enums.LibraryItemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class ThesisDAOImpl {
    private static final Logger logger = LoggerFactory.getLogger(ThesisDAOImpl.class);

    private static final String INSERT_THESIS = """
        INSERT INTO thesis (item_id, university, department, advisor)
        VALUES (?, ?, ?, ?)
    """;

    private static final String UPDATE_THESIS = """
        UPDATE thesis 
        SET university = ?, department = ?, advisor = ?
        WHERE item_id = ?
    """;

    private static final String FIND_THESIS_BY_ID = """
        SELECT li.*, t.university, t.department, t.advisor
        FROM library_item li
        JOIN thesis t ON li.id = t.item_id
        WHERE li.id = ?
    """;

    public void save(int itemId, Thesis thesis, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_THESIS)) {
            preparedStatement.setInt(1, itemId);
            preparedStatement.setString(2, thesis.getUniversity());
            preparedStatement.setString(3, thesis.getDepartment());
            preparedStatement.setString(4, thesis.getAdvisor());
            preparedStatement.executeUpdate();
            logger.debug("Saved thesis with item_id: {}", itemId);
        }
    }

    public void update(Thesis thesis, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_THESIS)) {
            preparedStatement.setString(1, thesis.getUniversity());
            preparedStatement.setString(2, thesis.getDepartment());
            preparedStatement.setString(3, thesis.getAdvisor());
            preparedStatement.setInt(4, thesis.getId());
            preparedStatement.executeUpdate();
            logger.debug("Updated thesis with id: {}", thesis.getId());
        }
    }

    public Thesis findById(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(FIND_THESIS_BY_ID);
            preparedStatement.setInt(1, id);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return extractThesisFromResultSet(resultSet);
            }

            logger.warn("Thesis not found with id: {}", id);
            return null;

        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    private Thesis extractThesisFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        LocalDate publishDate = resultSet.getDate("publish_date").toLocalDate();
        LibraryItemStatus status = LibraryItemStatus.valueOf(resultSet.getString("status"));
        String university = resultSet.getString("university");
        String department = resultSet.getString("department");
        String advisor = resultSet.getString("advisor");
        Date returnDate = resultSet.getDate("return_date");
        LocalDate returnLocalDate = returnDate != null ? returnDate.toLocalDate() : null;

        return new Thesis(id, title, author, status, publishDate,
                university, department, advisor, returnLocalDate);
    }
}