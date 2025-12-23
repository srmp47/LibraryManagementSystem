package library.database.dao.impl;

import library.database.dao.MagazineDAO;
import library.database.util.DBUtil;
import library.models.Magazine;
import library.models.enums.LibraryItemStatus;

import java.sql.Connection;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class MagazineDAOImpl extends BaseDAO implements MagazineDAO {
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

    @Override
    public void save(int itemId, Magazine magazine, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, INSERT_MAGAZINE,
                itemId, magazine.getIssueNumber(), magazine.getPublisher(), magazine.getCategory());
    }

    @Override
    public void update(Magazine magazine, Connection connection) throws SQLException {
        DBUtil.executeUpdate(connection, UPDATE_MAGAZINE,
                magazine.getIssueNumber(), magazine.getPublisher(), magazine.getCategory(), magazine.getId());
    }

    @Override
    public Magazine findById(int id) throws SQLException {
        return DBUtil.executeQueryAndMap(FIND_MAGAZINE_BY_ID, this::extractMagazineFromResultSet, id);
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