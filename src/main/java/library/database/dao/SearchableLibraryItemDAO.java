package library.database.dao;

import library.models.LibraryItem;
import library.models.enums.LibraryItemStatus;
import java.sql.SQLException;
import java.util.List;

public interface SearchableLibraryItemDAO {
    List<LibraryItem> findByTitle(String title) throws SQLException;
    List<LibraryItem> findByAuthor(String author) throws SQLException;
    List<LibraryItem> findByStatus(LibraryItemStatus status) throws SQLException;
}