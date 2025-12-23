package library.database.dao.impl;

import library.database.dao.*;
import library.database.util.DBUtil;
import library.models.*;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.sql.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class LibraryItemDAOImpl extends BaseDAO implements LibraryItemDAO {
    private static final String INSERT_ITEM = """
        INSERT INTO library_item (title, author, publish_date, status, type, return_date, user_id)
        VALUES (?, ?, ?, ?, ?, ?, ?)
    """;

    private static final String UPDATE_ITEM = """
        UPDATE library_item 
        SET title = ?, author = ?, publish_date = ?, status = ?, type = ?, 
            return_date = ?, user_id = ?
        WHERE id = ?
    """;

    private static final String DELETE_ITEM = "DELETE FROM library_item WHERE id = ?";
    private static final String FIND_BY_ID = "SELECT * FROM library_item WHERE id = ?";
    private static final String FIND_BY_TITLE = "SELECT * FROM library_item WHERE title LIKE ?";
    private static final String FIND_BY_AUTHOR = "SELECT * FROM library_item WHERE author LIKE ?";
    private static final String FIND_BY_STATUS = "SELECT * FROM library_item WHERE status = ?";
    private static final String FIND_ALL = "SELECT * FROM library_item ORDER BY id";
    private static final String BORROW_ITEM = """
        UPDATE library_item 
        SET status = 'BORROWED', return_date = ?, user_id = ?
        WHERE id = ? AND status = 'EXIST'
    """;
    private static final String RETURN_ITEM = """
        UPDATE library_item 
        SET status = 'EXIST', return_date = NULL, user_id = NULL
        WHERE id = ? AND status = 'BORROWED'
    """;
    private static final String UPDATE_STATUS = "UPDATE library_item SET status = ? WHERE id = ?";
    private static final String COUNT_BY_STATUS = "SELECT COUNT(*) FROM library_item WHERE status = ?";
    private static final String COUNT_ALL = "SELECT COUNT(*) FROM library_item";
    private static final String COUNT_OVERDUE = """
        SELECT COUNT(*) FROM library_item 
        WHERE status = 'BORROWED' AND return_date < CURDATE()
    """;

    private final BookDAO bookDAO;
    private final MagazineDAO magazineDAO;
    private final ReferenceDAO referenceDAO;
    private final ThesisDAO thesisDAO;

    public LibraryItemDAOImpl() {
        this.bookDAO = new BookDAOImpl();
        this.magazineDAO = new MagazineDAOImpl();
        this.referenceDAO = new ReferenceDAOImpl();
        this.thesisDAO = new ThesisDAOImpl();
    }

    @Override
    public int save(LibraryItem item) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet generatedKeys = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);
            setItemParameters(preparedStatement, item);
            preparedStatement.setInt(7, getDefaultUserId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating library item failed, no rows affected.");
            }

            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);
                saveSpecificItem(generatedId, item, connection);
                commitTransaction(connection);
                logger.info("Saved {} with ID: {}", item.getClass().getSimpleName(), generatedId);
                return generatedId;
            } else {
                throw new SQLException("Creating library item failed, no ID obtained.");
            }

        } catch (SQLException e) {
            rollbackTransaction(connection);
            logger.error("Error saving library item: {}", e.getMessage());
            throw e;
        } finally {
            DBUtil.closeResources(generatedKeys, preparedStatement, connection);
        }
    }

    private void setItemParameters(PreparedStatement stmt, LibraryItem item) throws SQLException {
        stmt.setString(1, item.getTitle());
        stmt.setString(2, item.getAuthor());
        stmt.setDate(3, Date.valueOf(item.getPublishDate()));
        stmt.setString(4, item.getStatus().name());
        stmt.setString(5, item.getType().name());

        if (item.getReturnDate() != null) {
            stmt.setDate(6, Date.valueOf(item.getReturnDate()));
        } else {
            stmt.setNull(6, Types.DATE);
        }
    }

    private void saveSpecificItem(int itemId, LibraryItem item, Connection connection) throws SQLException {
        switch (item.getType()) {
            case BOOK:
                bookDAO.save(itemId, (Book) item, connection);
                break;
            case MAGAZINE:
                magazineDAO.save(itemId, (Magazine) item, connection);
                break;
            case REFERENCE:
                referenceDAO.save(itemId, (Reference) item, connection);
                break;
            case THESIS:
                thesisDAO.save(itemId, (Thesis) item, connection);
                break;
        }
    }

    @Override
    public boolean update(LibraryItem item) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            preparedStatement = connection.prepareStatement(UPDATE_ITEM);
            setItemParameters(preparedStatement, item);
            preparedStatement.setInt(7, getDefaultUserId());
            preparedStatement.setInt(8, item.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                updateSpecificItem(item, connection);
                commitTransaction(connection);
                logger.info("Updated item with ID: {}", item.getId());
                return true;
            }

            return false;

        } catch (SQLException e) {
            rollbackTransaction(connection);
            logger.error("Error updating library item: {}", e.getMessage());
            throw e;
        } finally {
            DBUtil.closeResources(null, preparedStatement, connection);
        }
    }

    private void updateSpecificItem(LibraryItem item, Connection connection) throws SQLException {
        switch (item.getType()) {
            case BOOK:
                bookDAO.update((Book) item, connection);
                break;
            case MAGAZINE:
                magazineDAO.update((Magazine) item, connection);
                break;
            case REFERENCE:
                referenceDAO.update((Reference) item, connection);
                break;
            case THESIS:
                thesisDAO.update((Thesis) item, connection);
                break;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int affectedRows = DBUtil.executeUpdate(connection, DELETE_ITEM, id);
            commitTransaction(connection);

            logger.info("Deleted item with ID: {}", id);
            return affectedRows > 0;

        } catch (SQLException e) {
            rollbackTransaction(connection);
            logger.error("Error deleting library item: {}", e.getMessage());
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public Optional<LibraryItem> findById(int id) throws SQLException {
        return Optional.ofNullable(DBUtil.executeQueryAndMap(FIND_BY_ID, this::mapResultSetToLibraryItem, id));
    }

    @Override
    public List<LibraryItem> findByTitle(String title) throws SQLException {
        return findItems(FIND_BY_TITLE, "%" + title + "%");
    }

    @Override
    public List<LibraryItem> findByAuthor(String author) throws SQLException {
        return findItems(FIND_BY_AUTHOR, "%" + author + "%");
    }

    @Override
    public List<LibraryItem> findByStatus(LibraryItemStatus status) throws SQLException {
        return findItems(FIND_BY_STATUS, status.name());
    }

    @Override
    public List<LibraryItem> findAll() throws SQLException {
        return findItems(FIND_ALL, null);
    }

    private List<LibraryItem> findItems(String query, String param) throws SQLException {
        if (param != null) {
            return DBUtil.executeQueryAndMapList(query, this::mapResultSetToLibraryItem, param);
        } else {
            return DBUtil.executeQueryAndMapList(query, this::mapResultSetToLibraryItem);
        }
    }

    private LibraryItem mapResultSetToLibraryItem(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        LibraryItemType type = LibraryItemType.valueOf(resultSet.getString("type"));

        switch (type) {
            case BOOK:
                return bookDAO.findById(id);
            case MAGAZINE:
                return magazineDAO.findById(id);
            case REFERENCE:
                return referenceDAO.findById(id);
            case THESIS:
                return thesisDAO.findById(id);
            default:
                throw new SQLException("Unknown library item type: " + type);
        }
    }

    @Override
    public boolean borrowItem(int itemId, int userId, LocalDate returnDate) throws SQLException {
        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int affectedRows = DBUtil.executeUpdate(connection, BORROW_ITEM,
                    Date.valueOf(returnDate), getDefaultUserId(), itemId);
            commitTransaction(connection);

            if (affectedRows > 0) {
                logger.info("Item {} borrowed by user {}", itemId, getDefaultUserId());
                return true;
            }

            return false;

        } catch (SQLException e) {
            rollbackTransaction(connection);
            logger.error("Error borrowing item: {}", e.getMessage());
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public boolean returnItem(int itemId) throws SQLException {
        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int affectedRows = DBUtil.executeUpdate(connection, RETURN_ITEM, itemId);
            commitTransaction(connection);

            if (affectedRows > 0) {
                logger.info("Item {} returned", itemId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            rollbackTransaction(connection);
            logger.error("Error returning item: {}", e.getMessage());
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public boolean updateStatus(int itemId, LibraryItemStatus status) throws SQLException {
        Connection connection = null;

        try {
            connection = getConnection();
            connection.setAutoCommit(false);

            int affectedRows = DBUtil.executeUpdate(connection, UPDATE_STATUS, status.name(), itemId);
            commitTransaction(connection);

            logger.info("Updated status of item {} to {}", itemId, status);
            return affectedRows > 0;

        } catch (SQLException e) {
            rollbackTransaction(connection);
            logger.error("Error updating item status: {}", e.getMessage());
            throw e;
        } finally {
            closeConnection(connection);
        }
    }

    @Override
    public int countByStatus(LibraryItemStatus status) throws SQLException {
        return executeCountQuery(COUNT_BY_STATUS, status.name());
    }

    @Override
    public int countAll() throws SQLException {
        return executeCountQuery(COUNT_ALL, null);
    }

    @Override
    public int countOverdueItems() throws SQLException {
        return executeCountQuery(COUNT_OVERDUE, null);
    }

    private int executeCountQuery(String query, String param) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            preparedStatement = connection.prepareStatement(query);

            if (param != null) {
                preparedStatement.setString(1, param);
            }

            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                return resultSet.getInt(1);
            }

            return 0;

        } finally {
            DBUtil.closeResources(resultSet, preparedStatement, connection);
        }
    }
}