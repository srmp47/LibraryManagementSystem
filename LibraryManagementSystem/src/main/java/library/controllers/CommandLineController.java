package library.controllers;

import library.models.*;
import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Scanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineController {
    private Library getLibrary() {
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
        System.out.println("7. 📥 Borrow Library Item");
        System.out.println("8. 📤 Return Library Item");
        System.out.println("9. 📋 List Borrowed Items");
        System.out.println("10. 📊 Display Statistics");
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
                borrowLibraryItem();
                return false;
            case "8":
                returnLibraryItem();
                return false;
            case "9":
                listBorrowedItems();
                return false;
            case "10":
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

        LibraryItemStatus status = LibraryItemStatus.EXIST;

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

        Book book = new Book(null ,title, author, status, publishDate, isbn, genre, pageCount, null);
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

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter issue number: ");
        String issueNumber = scanner.nextLine().trim();

        System.out.print("Enter publisher: ");
        String publisher = scanner.nextLine().trim();

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        Magazine magazine = new Magazine(null ,title, editor, status, publishDate, issueNumber, publisher, category, null);
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

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter reference type (e.g., Dictionary, Encyclopedia): ");
        String referenceType = scanner.nextLine().trim();

        System.out.print("Enter edition: ");
        String edition = scanner.nextLine().trim();

        System.out.print("Enter subject: ");
        String subject = scanner.nextLine().trim();

        Reference reference = new Reference(null ,title, author, status, publishDate, referenceType, edition, subject, null);
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

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter university: ");
        String university = scanner.nextLine().trim();

        System.out.print("Enter department: ");
        String department = scanner.nextLine().trim();

        System.out.print("Enter advisor: ");
        String advisor = scanner.nextLine().trim();

        Thesis thesis = new Thesis(null, title, author, status, publishDate, university, department, advisor, null);
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
        HashMap<Integer, Integer> idByIndex = new HashMap<>();
        int index = 1;
        for (LibraryItem item : foundItems) {
            idByIndex.put(index, item.getId());
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

            int itemId = idByIndex.get(choice);
            LibraryItem itemToRemove = library.getLibraryItemById(itemId);

            if (itemToRemove != null) {
                if (itemToRemove.getStatus() == LibraryItemStatus.BORROWED) {
                    logger.warn("Attempt to remove borrowed item - Type: {}, Title: '{}', ID: {}",
                            itemToRemove.getClass().getSimpleName(), itemToRemove.getTitle(), itemToRemove.getId());
                    System.out.println("❌ Cannot remove borrowed item!");
                    System.out.println("📖 Item is currently borrowed. Please ensure it's returned first.");
                    return;
                }

                System.out.print("Are you sure you want to remove this item? (y/n): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();
                if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                    logger.info("User cancelled removal after confirmation");
                    System.out.println("❌ Removal cancelled.");
                    return;
                }

                library.removeLibraryItem(itemToRemove);
                logger.info("Successfully removed library item - Type: {}, Title: '{}', ID: {}, Status: {}",
                        itemToRemove.getClass().getSimpleName(), itemToRemove.getTitle(),
                        itemToRemove.getId(), itemToRemove.getStatus());
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
        HashMap<Integer, Integer> idByIndex = new HashMap<>();
        int index = 1;
        for (LibraryItem item : foundItems) {
            idByIndex.put(index, item.getId());
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
            int itemId = idByIndex.get(choice);
            LibraryItem itemToUpdate = library.getLibraryItemById(itemId);

            if (itemToUpdate != null) {
                LibraryItemStatus oldStatus = itemToUpdate.getStatus();
                LibraryItemStatus newStatus = null;
                while (newStatus == null) {
                    System.out.print("Enter new status (EXIST/BANNED): ");
                    String statusInput = scanner.nextLine().trim().toUpperCase();
                    try {
                        newStatus = LibraryItemStatus.valueOf(statusInput);
                        if(newStatus == LibraryItemStatus.BORROWED){
                            newStatus = null;
                            System.out.println("You can not change status to BORROWED!\nPlease select menu option 7");
                            logger.warn("User can not change status of library item to BORROWED directly");
                        }
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

    private void borrowLibraryItem() {
        logger.info("Starting borrowLibraryItem process");
        System.out.println("\n📥 === BORROW LIBRARY ITEM ===");

        if (library.getLibraryItems().isEmpty()) {
            logger.warn("Borrow operation failed: No items in library");
            System.out.println("❌ No items in library!");
            return;
        }

        System.out.print("Enter item title to borrow: ");
        String title = scanner.nextLine().trim();
        logger.info("User searching for item to borrow with title: '{}'", title);

        GenericLinkedList<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            logger.warn("No items found for borrowing with title containing: '{}'", title);
            System.out.println("❌ No items found with title containing: " + title);
            return;
        }

        GenericLinkedList<LibraryItem> availableItems = new GenericLinkedList<>();
        for (LibraryItem item : foundItems) {
            if (item.getStatus() == LibraryItemStatus.EXIST) {
                availableItems.add(item);
            }
        }

        if (availableItems.isEmpty()) {
            logger.warn("No available items found for borrowing with title: '{}'", title);
            System.out.println("❌ No available items found with title: " + title);
            System.out.println("All matching items are either borrowed or banned.");
            return;
        }

        logger.info("Found {} available items matching title '{}'", availableItems.getSize(), title);
        System.out.println("\n📚 Available Items for Borrowing:");
        HashMap<Integer, Integer> idByIndex = new HashMap<>();
        int index = 1;
        for (LibraryItem item : availableItems) {
            idByIndex.put(index, item.getId());
            System.out.printf("%d. ", index++);
            item.display();
        }

        System.out.print("Enter the number of item to borrow (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                logger.info("User cancelled borrow operation");
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > availableItems.getSize()) {
                logger.warn("Invalid choice for borrowing: {} (valid range: 1-{})", choice, availableItems.getSize());
                System.out.println("❌ Invalid choice!");
                return;
            }

            int itemId = idByIndex.get(choice);
            LibraryItem itemToBorrow = library.getLibraryItemById(itemId);
            LocalDate returnDate = LocalDate.now().plusDays(14);
            boolean success = library.borrowItem(itemToBorrow.getId(), returnDate);
            if (success) {
                logger.info("Successfully borrowed item - Title: '{}', ID: {}, Expected Return: {}",
                        itemToBorrow.getTitle(), itemToBorrow.getId(), returnDate);
                System.out.println("✅ Item borrowed successfully!");
                System.out.println("📅 Expected return date: " + returnDate);
            } else {
                logger.error("Failed to borrow item - Title: '{}', ID: {}", itemToBorrow.getTitle(), itemToBorrow.getId());
                System.out.println("❌ Failed to borrow item!");
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception during borrow operation", e);
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void returnLibraryItem() {
        logger.info("Starting returnLibraryItem process");
        System.out.println("\n📤 === RETURN LIBRARY ITEM ===");
        GenericLinkedList<LibraryItem> borrowedItems = library.getBorrowedItems();
        if (borrowedItems.isEmpty()) {
            logger.warn("Return operation failed: No borrowed items");
            System.out.println("❌ No borrowed items to return!");
            return;
        }

        System.out.println("\n📚 Borrowed Items:");
        HashMap<Integer, Integer> idByIndex = new HashMap<>();
        int index = 1;
        for (LibraryItem item : borrowedItems) {
            idByIndex.put(index, item.getId());
            System.out.printf("%d. ", index++);
            item.display();
            LocalDate expectedReturn = item.getReturnDate();
            if (expectedReturn != null) {
                System.out.println("   Expected Return: " + expectedReturn);
                if (expectedReturn.isBefore(LocalDate.now())) {
                    System.out.println("   ⚠️  OVERDUE!");
                }
            }
        }

        System.out.print("Enter the number of item to return (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                logger.info("User cancelled return operation");
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > borrowedItems.getSize()) {
                logger.warn("Invalid choice for return: {} (valid range: 1-{})", choice, borrowedItems.getSize());
                System.out.println("❌ Invalid choice!");
                return;
            }

            int itemId = idByIndex.get(choice);
            LibraryItem libraryItem = library.getLibraryItemById(itemId);
            LocalDate expectedReturn = libraryItem.getReturnDate();
            boolean success = library.returnItem(itemId);
            if (success) {
                LibraryItem returnedItem = library.getLibraryItemById(itemId);
                logger.info("Successfully returned item - Title: '{}', ID: {}",
                        returnedItem.getTitle(), returnedItem.getId());
                System.out.println("✅ Item returned successfully!");
                if (expectedReturn != null && expectedReturn.isBefore(LocalDate.now())) {
                    System.out.println("⚠️  Note: This item was returned late.");
                }
            } else {
                logger.error("Failed to return item - ID: {}", itemId);
                System.out.println("❌ Failed to return item!");
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception during return operation", e);
            System.out.println("❌ Please enter a valid number!");
        }
    }
    private void listBorrowedItems() {
        logger.info("Listing borrowed items");
        System.out.println("\n📋 === BORROWED LIBRARY ITEMS ===");

        GenericLinkedList<LibraryItem> borrowedItems = library.getBorrowedItems();
        if (borrowedItems.isEmpty()) {
            System.out.println("No borrowed items found.");
            return;
        }

        int count = 0;
        for (LibraryItem item : borrowedItems) {
            System.out.print((++count) + ". ");
            item.display();
            LocalDate expectedReturn = item.getReturnDate();
            if (expectedReturn != null) {
                System.out.println("   Expected Return: " + expectedReturn);
                if (expectedReturn.isBefore(LocalDate.now())) {
                    System.out.println("   ⚠️  STATUS: OVERDUE");
                } else {
                    System.out.println("   ✅ STATUS: On Time");
                }
            }
            System.out.println("------------------------");
        }
        System.out.println("--- Total: " + count + " borrowed items ---");
    }

    private void displayStatistics() {
        logger.info("Displaying library statistics");
        System.out.println("\n📊 === LIBRARY STATISTICS ===");

        int totalItems = library.getLibraryItems().getSize();
        int existCount = 0;
        int borrowedCount = 0;
        int bannedCount = 0;
        int overdueCount = 0;

        for (LibraryItem item : library.getLibraryItems()) {
            switch (item.getStatus()) {
                case EXIST: existCount++; break;
                case BORROWED:
                    borrowedCount++;
                    LocalDate expectedReturn = item.getReturnDate();
                    if (expectedReturn != null && expectedReturn.isBefore(LocalDate.now())) {
                        overdueCount++;
                    }
                    break;
                case BANNED: bannedCount++; break;
            }
        }

        logger.info("Statistics - Total: {}, Available: {}, Borrowed: {}, Banned: {}, Overdue: {}",
                totalItems, existCount, borrowedCount, bannedCount, overdueCount);

        System.out.println("Total Items: " + totalItems);
        System.out.println("Available (EXIST): " + existCount);
        System.out.println("Borrowed: " + borrowedCount);
        System.out.println("  ├─ On Time: " + (borrowedCount - overdueCount));
        System.out.println("  └─ Overdue: " + overdueCount);
        System.out.println("Banned: " + bannedCount);

        if (totalItems > 0) {
            double availablePercentage = (existCount * 100.0) / totalItems;
            double borrowedPercentage = (borrowedCount * 100.0) / totalItems;
            double bannedPercentage = (bannedCount * 100.0) / totalItems;

            System.out.println("\n📈 Percentages:");
            System.out.printf("Available: %.1f%%\n", availablePercentage);
            System.out.printf("Borrowed: %.1f%%\n", borrowedPercentage);
            System.out.printf("Banned: %.1f%%\n", bannedPercentage);
        }
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