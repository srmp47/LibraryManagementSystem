package library.controllers;

import library.models.*;
import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineController {
    public Library getLibrary() {
        return library;
    }

    private final Library library;
    private final Scanner scanner;
    private static final Logger logger = LoggerFactory.getLogger(CommandLineController.class);

    public CommandLineController() {
        logger.info("Initializing CommandLineController with new Library");
        this.library = new Library();
        this.scanner = new Scanner(System.in);
    }

    public CommandLineController(Library library){
        logger.info("Initializing CommandLineController with provided Library");
        this.scanner = new Scanner(System.in);
        this.library = library;
    }

    public void printMenu() {
        System.out.println("\n📚 === MAIN MENU ===");
        System.out.println("1. ➕ Add Library Item");
        System.out.println("2. 🗑️ Remove Library Item");
        System.out.println("3. 🔍 Search Library Items");
        System.out.println("4. 📖 List All Library Items");
        System.out.println("5. 📅 Sort Library Items by Publication Date");
        System.out.println("6. ✏️ Update Library Item Status");
        System.out.println("7. 📊 Display Statistics");
        System.out.println("0. 🚪 Exit");
        System.out.print("Enter your choice: ");
    }

    public String getUserInput() {
        return scanner.nextLine().trim();
    }

    public boolean handleChoice(String choice) {
        logger.info("User selected menu option: {}", choice);
        switch (choice) {
            case "1":
                addLibraryItem();
                return false;
            case "2":
                removeLibraryItem();
                return false;
            case "3":
                searchLibraryItems();
                return false;
            case "4":
                listAllLibraryItems();
                return false;
            case "5":
                sortLibraryItems();
                return false;
            case "6":
                updateLibraryItemStatus();
                return false;
            case "7":
                displayStatistics();
                return false;
            case "0":
                logger.info("User initiated program exit");
                return true;
            default:
                logger.warn("Invalid menu choice entered: {}", choice);
                System.out.println("❌ Invalid choice! Please try again.");
                return false;
        }
    }

    private void addLibraryItem() {
        logger.info("Starting addLibraryItem process");
        System.out.println("\n➕ === ADD NEW LIBRARY ITEM ===");

        System.out.println("Choose item type:");
        System.out.println("1. Book");
        System.out.println("2. Magazine");
        System.out.println("3. Reference");
        System.out.println("4. Thesis");
        System.out.print("Enter your choice: ");

        String typeChoice = scanner.nextLine().trim();
        logger.info("User selected item type: {}", typeChoice);

        switch (typeChoice) {
            case "1":
                addBook();
                break;
            case "2":
                addMagazine();
                break;
            case "3":
                addReference();
                break;
            case "4":
                addThesis();
                break;
            default:
                logger.warn("Invalid item type selected: {}", typeChoice);
                System.out.println("❌ Invalid type choice!");
                return;
        }
    }

    private void addBook() {
        logger.info("Starting addBook process");
        System.out.println("\n📘 === ADD NEW BOOK ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Book creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            logger.warn("Book creation failed for title '{}': Empty author provided", title);
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
                logger.debug("Parsed publish date for book '{}': {}", title, publishDate);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided for book '{}': {}", title, dateInput);
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
                logger.debug("Set status for book '{}': {}", title, status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided for book '{}': {}", title, statusInput);
                System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
            }
        }

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        int pageCount = 0;
        while (pageCount <= 0) {
            System.out.print("Enter page count (must be positive): ");
            try {
                pageCount = Integer.parseInt(scanner.nextLine().trim());
                if (pageCount <= 0) {
                    logger.warn("Invalid page count for book '{}': {}", title, pageCount);
                    System.out.println("❌ Page count must be positive!");
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid number format for page count of book '{}'", title);
                System.out.println("❌ Please enter a valid number!");
            }
        }

        Book book = new Book(null ,title, author, status, publishDate, isbn, genre, pageCount);
        library.addLibraryItem(book);
        logger.info("Successfully added new book - Title: '{}', Author: '{}', ISBN: '{}', Status: {}",
                title, author, isbn, status);
        System.out.println("✅ Book added successfully!");
    }

    private void addMagazine() {
        logger.info("Starting addMagazine process");
        System.out.println("\n📰 === ADD NEW MAGAZINE ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Magazine creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter editor: ");
        String editor = scanner.nextLine().trim();
        if (editor.isEmpty()) {
            logger.warn("Magazine creation failed for title '{}': Empty editor provided", title);
            System.out.println("❌ Editor cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
                logger.debug("Parsed publish date for magazine '{}': {}", title, publishDate);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided for magazine '{}': {}", title, dateInput);
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
                logger.debug("Set status for magazine '{}': {}", title, status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided for magazine '{}': {}", title, statusInput);
                System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
            }
        }

        System.out.print("Enter issue number: ");
        String issueNumber = scanner.nextLine().trim();

        System.out.print("Enter publisher: ");
        String publisher = scanner.nextLine().trim();

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        Magazine magazine = new Magazine(null ,title, editor, status, publishDate, issueNumber, publisher, category);
        library.addLibraryItem(magazine);
        logger.info("Successfully added new magazine - Title: '{}', Editor: '{}', Issue: '{}', Status: {}",
                title, editor, issueNumber, status);
        System.out.println("✅ Magazine added successfully!");
    }

    private void addReference() {
        logger.info("Starting addReference process");
        System.out.println("\n📚 === ADD NEW REFERENCE ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Reference creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            logger.warn("Reference creation failed for title '{}': Empty author provided", title);
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
                logger.debug("Parsed publish date for reference '{}': {}", title, publishDate);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided for reference '{}': {}", title, dateInput);
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
                logger.debug("Set status for reference '{}': {}", title, status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided for reference '{}': {}", title, statusInput);
                System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
            }
        }

        System.out.print("Enter reference type (e.g., Dictionary, Encyclopedia): ");
        String referenceType = scanner.nextLine().trim();

        System.out.print("Enter edition: ");
        String edition = scanner.nextLine().trim();

        System.out.print("Enter subject: ");
        String subject = scanner.nextLine().trim();

        Reference reference = new Reference(null ,title, author, status, publishDate, referenceType, edition, subject);
        library.addLibraryItem(reference);
        logger.info("Successfully added new reference - Title: '{}', Author: '{}', Type: '{}', Status: {}",
                title, author, referenceType, status);
        System.out.println("✅ Reference added successfully!");
    }

    private void addThesis() {
        logger.info("Starting addThesis process");
        System.out.println("\n🎓 === ADD NEW THESIS ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Thesis creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            logger.warn("Thesis creation failed for title '{}': Empty author provided", title);
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
                logger.debug("Parsed publish date for thesis '{}': {}", title, publishDate);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided for thesis '{}': {}", title, dateInput);
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
                logger.debug("Set status for thesis '{}': {}", title, status);
            } catch (IllegalArgumentException e) {
                logger.warn("Invalid status provided for thesis '{}': {}", title, statusInput);
                System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
            }
        }

        System.out.print("Enter university: ");
        String university = scanner.nextLine().trim();

        System.out.print("Enter department: ");
        String department = scanner.nextLine().trim();

        System.out.print("Enter advisor: ");
        String advisor = scanner.nextLine().trim();

        Thesis thesis = new Thesis(null, title, author, status, publishDate, university, department, advisor);
        library.addLibraryItem(thesis);
        logger.info("Successfully added new thesis - Title: '{}', Author: '{}', University: '{}', Status: {}",
                title, author, university, status);
        System.out.println("✅ Thesis added successfully!");
    }

    private void removeLibraryItem() {
        logger.info("Starting removeLibraryItem process");
        System.out.println("\n🗑️ === REMOVE LIBRARY ITEM ===");

        if (library.getLibraryItems().isEmpty()) {
            logger.warn("Remove operation failed: No items in library");
            System.out.println("❌ No items in library!");
            return;
        }

        System.out.print("Enter item title to remove: ");
        String title = scanner.nextLine().trim();
        logger.info("User searching for item to remove with title: '{}'", title);

        GenericLinkedList<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            logger.warn("No items found for removal with title containing: '{}'", title);
            System.out.println("❌ No items found with title containing: " + title);
            return;
        }

        logger.info("Found {} items matching title '{}'", foundItems.getSize(), title);
        System.out.println("\n📚 Found Items:");
        int index = 1;
        for (LibraryItem item : foundItems) {
            System.out.printf("%d. ", index++);
            item.display();
        }

        System.out.print("Enter the number of item to remove (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                logger.info("User cancelled remove operation");
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > foundItems.getSize()) {
                logger.warn("Invalid choice for removal: {} (valid range: 1-{})", choice, foundItems.getSize());
                System.out.println("❌ Invalid choice!");
                return;
            }

            LibraryItem itemToRemove = null;
            int currentIndex = 1;
            for (LibraryItem item : foundItems) {
                if (currentIndex == choice) {
                    itemToRemove = item;
                    break;
                }
                currentIndex++;
            }

            if (itemToRemove != null) {
                library.removeLibraryItem(itemToRemove);
                logger.info("Successfully removed library item - Type: {}, Title: '{}', ID: {}",
                        itemToRemove.getClass().getSimpleName(), itemToRemove.getTitle(), itemToRemove.getId());
                System.out.println("✅ Item removed successfully!");
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception during remove operation", e);
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void searchLibraryItems() {
        logger.info("Starting searchLibraryItems process");
        System.out.println("\n🔍 === SEARCH LIBRARY ITEMS ===");

        System.out.print("Enter search keyword (title or author): ");
        String keyword = scanner.nextLine().trim();
        logger.info("User searching with keyword: '{}'", keyword);

        if (keyword.isEmpty()) {
            logger.warn("Search failed: Empty keyword provided");
            System.out.println("❌ Search keyword cannot be empty!");
            return;
        }

        GenericLinkedList<LibraryItem> results = library.search(keyword);
        logger.info("Search completed for '{}': found {} results", keyword, results.getSize());
        displayLibraryItems(results, "Search Results for: '" + keyword + "'");
    }

    private void listAllLibraryItems() {
        logger.info("Listing all library items");
        System.out.println("\n📖 === ALL LIBRARY ITEMS ===");
        int itemCount = library.getLibraryItems().getSize();
        logger.info("Displaying all {} library items", itemCount);
        displayLibraryItems(library.getLibraryItems(), "All Items in Library");
    }

    private void sortLibraryItems() {
        logger.info("Starting sortLibraryItems process");
        System.out.println("\n📅 === SORTED LIBRARY ITEMS ===");
        GenericLinkedList<LibraryItem> sortedItems = library.sortLibraryItems();
        logger.info("Sorting completed: {} items sorted by publication date", sortedItems.getSize());
        displayLibraryItems(sortedItems, "Items Sorted by Publication Date (Newest First)");
    }

    private void updateLibraryItemStatus() {
        logger.info("Starting updateLibraryItemStatus process");
        System.out.println("\n✏️ === UPDATE LIBRARY ITEM STATUS ===");

        if (library.getLibraryItems().isEmpty()) {
            logger.warn("Status update failed: No items in library");
            System.out.println("❌ No items in library!");
            return;
        }

        System.out.print("Enter item title to update: ");
        String title = scanner.nextLine().trim();
        logger.info("User searching for item to update with title: '{}'", title);

        GenericLinkedList<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            logger.warn("No items found for status update with title containing: '{}'", title);
            System.out.println("❌ No items found with title containing: " + title);
            return;
        }

        logger.info("Found {} items for status update matching title '{}'", foundItems.getSize(), title);
        System.out.println("\n📚 Found Items:");
        int index = 1;
        for (LibraryItem item : foundItems) {
            System.out.printf("%d. ", index++);
            item.display();
        }

        System.out.print("Enter the number of item to update (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                logger.info("User cancelled status update operation");
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > foundItems.getSize()) {
                logger.warn("Invalid choice for status update: {} (valid range: 1-{})", choice, foundItems.getSize());
                System.out.println("❌ Invalid choice!");
                return;
            }

            LibraryItem itemToUpdate = null;
            int currentIndex = 1;
            for (LibraryItem item : foundItems) {
                if (currentIndex == choice) {
                    itemToUpdate = item;
                    break;
                }
                currentIndex++;
            }

            if (itemToUpdate != null) {
                LibraryItemStatus oldStatus = itemToUpdate.getStatus();
                LibraryItemStatus newStatus = null;
                while (newStatus == null) {
                    System.out.print("Enter new status (EXIST/BORROWED/BANNED): ");
                    String statusInput = scanner.nextLine().trim().toUpperCase();
                    try {
                        newStatus = LibraryItemStatus.valueOf(statusInput);
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid status provided for update: {}", statusInput);
                        System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
                    }
                }

                itemToUpdate.setStatus(newStatus);
                logger.info("Successfully updated item status - Item: '{}' (ID: {}), Old Status: {}, New Status: {}",
                        itemToUpdate.getTitle(), itemToUpdate.getId(), oldStatus, newStatus);
                System.out.println("✅ Item status updated successfully!");
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception during status update operation", e);
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void displayStatistics() {
        logger.info("Displaying library statistics");
        System.out.println("\n📊 === LIBRARY STATISTICS ===");

        int totalItems = library.getLibraryItems().getSize();
        int existCount = 0;
        int borrowedCount = 0;
        int bannedCount = 0;

        for (LibraryItem item : library.getLibraryItems()) {
            switch (item.getStatus()) {
                case EXIST: existCount++; break;
                case BORROWED: borrowedCount++; break;
                case BANNED: bannedCount++; break;
            }
        }

        logger.info("Statistics - Total: {}, Available: {}, Borrowed: {}, Banned: {}",
                totalItems, existCount, borrowedCount, bannedCount);

        System.out.println("Total Items: " + totalItems);
        System.out.println("Available (EXIST): " + existCount);
        System.out.println("Borrowed: " + borrowedCount);
        System.out.println("Banned: " + bannedCount);
    }

    private void displayLibraryItems(Iterable<LibraryItem> items, String title) {
        System.out.println("\n=== " + title + " ===");
        int count = 0;
        for (LibraryItem item : items) {
            System.out.print((++count) + ". ");
            item.display();
        }

        if (count == 0) {
            System.out.println("No items found.");
        } else {
            System.out.println("--- Total: " + count + " items ---");
        }
    }

    public void exitProgram() {
        logger.info("Initiating program exit sequence");
        System.out.println("\n💾 Saving data to file...");
        library.writeToFile();
        logger.info("Program exit completed successfully");
        System.out.println("👋 Thank you for using Library Management System!");
    }
}