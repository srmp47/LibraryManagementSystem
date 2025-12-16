package library.database.dao.impl;

import library.database.DatabaseConnection;
import library.database.dao.LibraryItemDAO;
import library.models.*;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LibraryItemDAOImpl implements LibraryItemDAO {
    private static final Logger logger = LoggerFactory.getLogger(LibraryItemDAOImpl.class);

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

    private final BookDAOImpl bookDAO;
    private final MagazineDAOImpl magazineDAO;
    private final ReferenceDAOImpl referenceDAO;
    private final ThesisDAOImpl thesisDAO;

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
            connection = DatabaseConnection.getConnection();

            preparedStatement = connection.prepareStatement(INSERT_ITEM, Statement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getAuthor());
            preparedStatement.setDate(3, Date.valueOf(item.getPublishDate()));
            preparedStatement.setString(4, item.getStatus().name());
            preparedStatement.setString(5, item.getType().name());

            if (item.getReturnDate() != null) {
                preparedStatement.setDate(6, Date.valueOf(item.getReturnDate()));
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }

            preparedStatement.setInt(7, DatabaseConnection.getDefaultUserId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows == 0) {
                throw new SQLException("Creating library item failed, no rows affected.");
            }

            generatedKeys = preparedStatement.getGeneratedKeys();
            if (generatedKeys.next()) {
                int generatedId = generatedKeys.getInt(1);

                saveSpecificItem(generatedId, item, connection);

                DatabaseConnection.commitTransaction(connection);
                logger.info("Saved {} with ID: {}", item.getClass().getSimpleName(), generatedId);
                return generatedId;
            } else {
                throw new SQLException("Creating library item failed, no ID obtained.");
            }

        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(connection);
            logger.error("Error saving library item: {}", e.getMessage());
            throw e;
        } finally {
            if (generatedKeys != null) generatedKeys.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    private void saveSpecificItem(int itemId, LibraryItem item, Connection connection) throws SQLException {
        switch (item.getType()) {
            case BOOK:
                Book book = (Book) item;
                bookDAO.save(itemId, book, connection);
                break;
            case MAGAZINE:
                Magazine magazine = (Magazine) item;
                magazineDAO.save(itemId, magazine, connection);
                break;
            case REFERENCE:
                Reference reference = (Reference) item;
                referenceDAO.save(itemId, reference, connection);
                break;
            case THESIS:
                Thesis thesis = (Thesis) item;
                thesisDAO.save(itemId, thesis, connection);
                break;
        }
    }

    @Override
    public boolean update(LibraryItem item) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();

            preparedStatement = connection.prepareStatement(UPDATE_ITEM);
            preparedStatement.setString(1, item.getTitle());
            preparedStatement.setString(2, item.getAuthor());
            preparedStatement.setDate(3, Date.valueOf(item.getPublishDate()));
            preparedStatement.setString(4, item.getStatus().name());
            preparedStatement.setString(5, item.getType().name());

            if (item.getReturnDate() != null) {
                preparedStatement.setDate(6, Date.valueOf(item.getReturnDate()));
            } else {
                preparedStatement.setNull(6, Types.DATE);
            }

            preparedStatement.setInt(7, DatabaseConnection.getDefaultUserId());
            preparedStatement.setInt(8, item.getId());

            int affectedRows = preparedStatement.executeUpdate();

            if (affectedRows > 0) {
                updateSpecificItem(item, connection);
                DatabaseConnection.commitTransaction(connection);
                logger.info("Updated item with ID: {}", item.getId());
                return true;
            }

            return false;

        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(connection);
            logger.error("Error updating library item: {}", e.getMessage());
            throw e;
        } finally {
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    private void updateSpecificItem(LibraryItem item, Connection connection) throws SQLException {
        switch (item.getType()) {
            case BOOK:
                Book book = (Book) item;
                bookDAO.update(book, connection);
                break;
            case MAGAZINE:
                Magazine magazine = (Magazine) item;
                magazineDAO.update(magazine, connection);
                break;
            case REFERENCE:
                Reference reference = (Reference) item;
                referenceDAO.update(reference, connection);
                break;
            case THESIS:
                Thesis thesis = (Thesis) item;
                thesisDAO.update(thesis, connection);
                break;
        }
    }

    @Override
    public boolean delete(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();

            preparedStatement = connection.prepareStatement(DELETE_ITEM);
            preparedStatement.setInt(1, id);

            int affectedRows = preparedStatement.executeUpdate();
            DatabaseConnection.commitTransaction(connection);

            logger.info("Deleted item with ID: {}", id);
            return affectedRows > 0;

        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(connection);
            logger.error("Error deleting library item: {}", e.getMessage());
            throw e;
        } finally {
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public Optional<LibraryItem> findById(int id) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet rs = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(FIND_BY_ID);
            preparedStatement.setInt(1, id);
            rs = preparedStatement.executeQuery();

            if (rs.next()) {
                LibraryItem item = mapResultSetToLibraryItem(rs);
                return Optional.of(item);
            }

            return Optional.empty();

        } finally {
            if (rs != null) rs.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
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
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        List<LibraryItem> items = new ArrayList<>();

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(query);

            if (param != null) {
                preparedStatement.setString(1, param);
            }

            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                LibraryItem item = mapResultSetToLibraryItem(resultSet);
                items.add(item);
            }

            return items;

        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    private LibraryItem mapResultSetToLibraryItem(ResultSet resultSet) throws SQLException {
        int id = resultSet.getInt("id");
        String title = resultSet.getString("title");
        String author = resultSet.getString("author");
        LocalDate publishDate = resultSet.getDate("publish_date").toLocalDate();
        LibraryItemStatus status = LibraryItemStatus.valueOf(resultSet.getString("status"));
        LibraryItemType type = LibraryItemType.valueOf(resultSet.getString("type"));
        Date returnDate = resultSet.getDate("return_date");
        LocalDate returnLocalDate = returnDate != null ? returnDate.toLocalDate() : null;

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
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(BORROW_ITEM);
            preparedStatement.setDate(1, Date.valueOf(returnDate));

            preparedStatement.setInt(2, DatabaseConnection.getDefaultUserId());
            preparedStatement.setInt(3, itemId);

            int affectedRows = preparedStatement.executeUpdate();
            DatabaseConnection.commitTransaction(connection);

            if (affectedRows > 0) {
                logger.info("Item {} borrowed by user {}", itemId, DatabaseConnection.getDefaultUserId());
                return true;
            }

            return false;

        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(connection);
            logger.error("Error borrowing item: {}", e.getMessage());
            throw e;
        } finally {
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public boolean returnItem(int itemId) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(RETURN_ITEM);
            preparedStatement.setInt(1, itemId);

            int affectedRows = preparedStatement.executeUpdate();
            DatabaseConnection.commitTransaction(connection);

            if (affectedRows > 0) {
                logger.info("Item {} returned", itemId);
                return true;
            }

            return false;

        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(connection);
            logger.error("Error returning item: {}", e.getMessage());
            throw e;
        } finally {
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }

    @Override
    public boolean updateStatus(int itemId, LibraryItemStatus status) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            preparedStatement = connection.prepareStatement(UPDATE_STATUS);
            preparedStatement.setString(1, status.name());
            preparedStatement.setInt(2, itemId);

            int affectedRows = preparedStatement.executeUpdate();
            DatabaseConnection.commitTransaction(connection);

            logger.info("Updated status of item {} to {}", itemId, status);
            return affectedRows > 0;

        } catch (SQLException e) {
            DatabaseConnection.rollbackTransaction(connection);
            logger.error("Error updating item status: {}", e.getMessage());
            throw e;
        } finally {
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
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
            connection = DatabaseConnection.getConnection();
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
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            DatabaseConnection.closeConnection(connection);
        }
    }
}