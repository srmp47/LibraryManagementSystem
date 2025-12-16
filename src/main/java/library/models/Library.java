package library.models;

import library.database.dao.LibraryItemDAO;
import library.database.dao.impl.LibraryItemDAOImpl;
import library.models.enums.LibraryItemStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

public class Library {
    private static Library library = null;
    private final LibraryItemDAO libraryItemDAO;
    private static final Logger logger = LoggerFactory.getLogger(Library.class);

    private Library() {
        this.libraryItemDAO = new LibraryItemDAOImpl();
        logger.info("Library instance created with JDBC database connection");
    }

    public static synchronized Library getInstance() {
        if (library == null) {
            library = new Library();
        }
        return library;
    }

    public List<LibraryItem> getLibraryItems() {
        try {
            return libraryItemDAO.findAll();
        } catch (SQLException e) {
            logger.error("Error getting all library items: {}", e.getMessage());
            throw new RuntimeException("Failed to get library items from database", e);
        }
    }

    public void addLibraryItem(LibraryItem libraryItem) {
        try {
            int generatedId = libraryItemDAO.save(libraryItem);
            logger.info("Added new {} with ID: {}",
                    libraryItem.getClass().getSimpleName(), generatedId);
        } catch (SQLException e) {
            logger.error("Error adding library item: {}", e.getMessage());
            throw new RuntimeException("Failed to add library item to database", e);
        }
    }

    public void removeLibraryItem(LibraryItem libraryItem) {
        try {
            boolean success = libraryItemDAO.delete(libraryItem.getId());
            if (success) {
                logger.info("Removed library item with ID: {}", libraryItem.getId());
            } else {
                logger.warn("Failed to remove library item with ID: {}", libraryItem.getId());
            }
        } catch (SQLException e) {
            logger.error("Error removing library item: {}", e.getMessage());
            throw new RuntimeException("Failed to remove library item from database", e);
        }
    }

    public LibraryItem getLibraryItemById(int id) {
        try {
            Optional<LibraryItem> item = libraryItemDAO.findById(id);
            return item.orElse(null);
        } catch (SQLException e) {
            logger.error("Error getting library item by ID {}: {}", id, e.getMessage());
            throw new RuntimeException("Failed to get library item from database", e);
        }
    }

    public boolean borrowItem(int itemId, LocalDate expectedReturnDate) {
        try {
            return libraryItemDAO.borrowItem(itemId, 0, expectedReturnDate);
        } catch (SQLException e) {
            logger.error("Error borrowing item {}: {}", itemId, e.getMessage());
            throw new RuntimeException("Failed to borrow item", e);
        }
    }

    public boolean returnItem(int itemId) {
        try {
            return libraryItemDAO.returnItem(itemId);
        } catch (SQLException e) {
            logger.error("Error returning item {}: {}", itemId, e.getMessage());
            throw new RuntimeException("Failed to return item", e);
        }
    }

    public List<LibraryItem> search(String keyword) {
        try {
            List<LibraryItem> byTitle = libraryItemDAO.findByTitle(keyword);
            List<LibraryItem> byAuthor = libraryItemDAO.findByAuthor(keyword);

            byTitle.forEach(item -> {
                if (!byAuthor.stream().anyMatch(i -> i.getId() == item.getId())) {
                    byAuthor.add(item);
                }
            });

            return byAuthor;
        } catch (SQLException e) {
            logger.error("Error searching for '{}': {}", keyword, e.getMessage());
            throw new RuntimeException("Failed to search library items", e);
        }
    }

    public List<LibraryItem> getBorrowedItems() {
        try {
            return libraryItemDAO.findByStatus(LibraryItemStatus.BORROWED);
        } catch (SQLException e) {
            logger.error("Error getting borrowed items: {}", e.getMessage());
            throw new RuntimeException("Failed to get borrowed items", e);
        }
    }

    public List<LibraryItem> sortLibraryItems() {
        List<LibraryItem> items = getLibraryItems();
        items.sort((a, b) -> b.getPublishDate().compareTo(a.getPublishDate()));
        return items;
    }

    public boolean updateItemStatus(int itemId, LibraryItemStatus newStatus) {
        try {
            return libraryItemDAO.updateStatus(itemId, newStatus);
        } catch (SQLException e) {
            logger.error("Error updating item status: {}", e.getMessage());
            throw new RuntimeException("Failed to update item status", e);
        }
    }

    public int getTotalItems() {
        try {
            return libraryItemDAO.countAll();
        } catch (SQLException e) {
            logger.error("Error counting total items: {}", e.getMessage());
            throw new RuntimeException("Failed to get total items count", e);
        }
    }

    public int getAvailableItems() {
        try {
            return libraryItemDAO.countByStatus(LibraryItemStatus.EXIST);
        } catch (SQLException e) {
            logger.error("Error counting available items: {}", e.getMessage());
            throw new RuntimeException("Failed to get available items count", e);
        }
    }

    public int getBorrowedItemsCount() {
        try {
            return libraryItemDAO.countByStatus(LibraryItemStatus.BORROWED);
        } catch (SQLException e) {
            logger.error("Error counting borrowed items: {}", e.getMessage());
            throw new RuntimeException("Failed to get borrowed items count", e);
        }
    }

    public int getBannedItemsCount() {
        try {
            return libraryItemDAO.countByStatus(LibraryItemStatus.BANNED);
        } catch (SQLException e) {
            logger.error("Error counting banned items: {}", e.getMessage());
            throw new RuntimeException("Failed to get banned items count", e);
        }
    }

    public int getOverdueItemsCount() {
        try {
            return libraryItemDAO.countOverdueItems();
        } catch (SQLException e) {
            logger.error("Error counting overdue items: {}", e.getMessage());
            throw new RuntimeException("Failed to get overdue items count", e);
        }
    }

    public void writeToFile() {
        logger.info("Data persistence is handled automatically by the database");
    }
}