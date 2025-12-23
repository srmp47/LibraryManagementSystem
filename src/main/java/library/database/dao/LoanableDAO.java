package library.database.dao;

import library.models.enums.LibraryItemStatus;
import java.sql.SQLException;
import java.time.LocalDate;

public interface LoanableDAO {
    boolean borrowItem(int itemId, int userId, LocalDate returnDate) throws SQLException;
    boolean returnItem(int itemId) throws SQLException;
    boolean updateStatus(int itemId, LibraryItemStatus status) throws SQLException;
}