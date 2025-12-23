package library.database.dao.impl;

import library.database.dao.ThesisDAO;
import library.database.util.DBUtil;
import library.models.Thesis;
import library.models.enums.LibraryItemStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class ThesisDAOImpl extends BaseDAO implements ThesisDAO {
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

    @Override
    public void save(int itemId, Thesis thesis, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, INSERT_THESIS,
                itemId, thesis.getUniversity(), thesis.getDepartment(), thesis.getAdvisor());
        logger.debug("Saved thesis with item_id: {}", itemId);
    }

    @Override
    public void update(Thesis thesis, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, UPDATE_THESIS,
                thesis.getUniversity(), thesis.getDepartment(), thesis.getAdvisor(), thesis.getId());
        logger.debug("Updated thesis with id: {}", thesis.getId());
    }

    @Override
    public Thesis findById(int id) throws SQLException {
        return DBUtil.executeQueryAndMap(FIND_THESIS_BY_ID, this::extractThesisFromResultSet, id);
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