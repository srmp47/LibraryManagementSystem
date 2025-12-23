package library.database.dao;

import library.models.LibraryItem;

public interface LibraryItemDAO extends
        CrudDAO<LibraryItem>,
        SearchableLibraryItemDAO,
        LoanableDAO,
        LibraryStatisticsDAO {
}