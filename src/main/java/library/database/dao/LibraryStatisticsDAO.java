package library.database.dao;

import library.models.enums.LibraryItemStatus;
import java.sql.SQLException;

public interface LibraryStatisticsDAO {
    int countByStatus(LibraryItemStatus status) throws SQLException;
    int countAll() throws SQLException;
    int countOverdueItems() throws SQLException;
}