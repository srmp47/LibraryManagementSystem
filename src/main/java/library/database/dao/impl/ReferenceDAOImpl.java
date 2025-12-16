package library.database.dao.impl;

import library.database.DatabaseConnection;
import library.models.Reference;
import library.models.enums.LibraryItemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;

public class ReferenceDAOImpl {
    private static final Logger logger = LoggerFactory.getLogger(ReferenceDAOImpl.class);

    private static final String INSERT_REFERENCE = """
        INSERT INTO reference (item_id, reference_type, edition, subject)
        VALUES (?, ?, ?, ?)
    """;

    private static final String UPDATE_REFERENCE = """
        UPDATE reference 
        SET reference_type = ?, edition = ?, subject = ?
        WHERE item_id = ?
    """;

    private static final String FIND_REFERENCE_BY_ID = """
        SELECT li.*, r.reference_type, r.edition, r.subject
        FROM library_item li
        JOIN reference r ON li.id = r.item_id
        WHERE li.id = ?
    """;

    public void save(int itemId, Reference reference, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(INSERT_REFERENCE)) {
            preparedStatement.setInt(1, itemId);
            preparedStatement.setString(2, reference.getReferenceType());
            preparedStatement.setString(3, reference.getEdition());
            preparedStatement.setString(4, reference.getSubject());
            preparedStatement.executeUpdate();
            logger.debug("Saved reference with item_id: {}", itemId);
        }
    }

    public void update(Reference reference, Connection connection) throws SQLException {
        try (PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_REFERENCE)) {
            preparedStatement.setString(1, reference.getReferenceType());
            preparedStatement.setString(2, reference.getEdition());
            preparedStatement.setString(3, reference.getSubject());
            preparedStatement.setInt(4, reference.getId());
            preparedStatement.executeUpdate();
            logger.debug("Updated reference with id: {}", reference.getId());
        }
    }

    public Reference findById(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(FIND_REFERENCE_BY_ID);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                return extractReferenceFromResultSet(rs);
            }

            logger.warn("Reference not found with id: {}", id);
            return null;

        } finally {
            if (rs != null) rs.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    private Reference extractReferenceFromResultSet(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        LocalDate publishDate = resultSet.getDate("publish_date").toLocalDate();
        LibraryItemStatus status = LibraryItemStatus.valueOf(resultSet.getString("status"));
        String referenceType = resultSet.getString("reference_type");
        String edition = resultSet.getString("edition");
        String subject = resultSet.getString("subject");
        Date returnDate = resultSet.getDate("return_date");
        LocalDate returnLocalDate = returnDate != null ? returnDate.toLocalDate() : null;

        return new Reference(id, title, author, status, publishDate,
                referenceType, edition, subject, returnLocalDate);
    }
}