package library.database.dao.impl;

import library.database.dao.ReferenceDAO;
import library.database.util.DBUtil;
import library.models.Reference;
import library.models.enums.LibraryItemStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ReferenceDAOImpl extends BaseDAO implements ReferenceDAO {
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

    @Override
    public void save(int itemId, Reference reference, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, INSERT_REFERENCE,
                itemId, reference.getReferenceType(), reference.getEdition(), reference.getSubject());
        logger.debug("Saved reference with item_id: {}", itemId);
    }

    @Override
    public void update(Reference reference, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, UPDATE_REFERENCE,
                reference.getReferenceType(), reference.getEdition(), reference.getSubject(), reference.getId());
        logger.debug("Updated reference with id: {}", reference.getId());
    }

    @Override
    public Reference findById(int id) throws SQLException {
        return DBUtil.executeQueryAndMap(FIND_REFERENCE_BY_ID, this::extractReferenceFromResultSet, id);
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