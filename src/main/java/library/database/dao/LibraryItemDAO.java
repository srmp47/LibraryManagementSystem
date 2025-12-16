package library.database.dao;

import library.models.*;
import library.models.enums.LibraryItemStatus;
import library.models.enums.LibraryItemType;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public interface LibraryItemDAO {
    int save(LibraryItem item) throws SQLException;
    boolean update(LibraryItem item) throws SQLException;
    boolean delete(int id) throws SQLException;
    Optional<LibraryItem> findById(int id) throws SQLException;

    List<LibraryItem> findByTitle(String title) throws SQLException;
    List<LibraryItem> findByAuthor(String author) throws SQLException;
    List<LibraryItem> findByStatus(LibraryItemStatus status) throws SQLException;
    List<LibraryItem> findAll() throws SQLException;

    boolean borrowItem(int itemId, int userId, LocalDate returnDate) throws SQLException;
    boolean returnItem(int itemId) throws SQLException;
    boolean updateStatus(int itemId, LibraryItemStatus status) throws SQLException;

    int countByStatus(LibraryItemStatus status) throws SQLException;
    int countAll() throws SQLException;
    int countOverdueItems() throws SQLException;
}