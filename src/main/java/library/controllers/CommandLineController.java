package library.controllers;

import library.models.*;
import library.models.enums.RequestType;
import library.models.enums.LibraryItemStatus;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommandLineController {
    private static CommandLineController commandLineController = null;
    private final Library library;
    private final Scanner scanner;
    private static final Logger logger = LoggerFactory.getLogger(CommandLineController.class);

    private CommandLineController(Library library) {
        logger.info("Initializing CommandLineController with provided Library");
        this.library = library;
        this.scanner = new Scanner(System.in);
    }
    public static CommandLineController getInstance(Library library) {
        if (commandLineController == null) {
            commandLineController = new CommandLineController(library);
            logger.info("Created new instance of CommandLineController");
        }
        return commandLineController;
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

    public LibraryRequest handleChoice(String choice) {
        logger.info("User selected menu option: {}", choice);
        switch (choice) {
            case "1":
                return addLibraryItem();
            case "2":
                return removeLibraryItem();
            case "3":
                searchLibraryItems();
                return null;
            case "4":
                listAllLibraryItems();
                return null;
            case "5":
                sortLibraryItems();
                return null;
            case "6":
                return updateLibraryItemStatus();
            case "7":
                return borrowLibraryItem();
            case "8":
                return returnLibraryItem();
            case "9":
                listBorrowedItems();
                return null;
            case "10":
                displayStatistics();
                return null;
            case "0":
                logger.info("User initiated program exit");
                return new LibraryRequest(RequestType.EXIT, -1); // Special exit request
            default:
                logger.warn("Invalid menu choice entered: {}", choice);
                System.out.println("❌ Invalid choice! Please try again.");
                return null;
        }
    }

    private LibraryRequest addLibraryItem() {
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
                return createAddBookRequest();
            case "2":
                return createAddMagazineRequest();
            case "3":
                return createAddReferenceRequest();
            case "4":
                return createAddThesisRequest();
            default:
                logger.warn("Invalid item type selected: {}", typeChoice);
                System.out.println("❌ Invalid type choice!");
                return null;
        }
    }

    private LibraryRequest createAddBookRequest() {
        logger.info("Start creating addBook request");
        System.out.println("\n📘 === ADD NEW BOOK ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Book creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return null;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            logger.warn("Book creation failed for title '{}': Empty author provided", title);
            System.out.println("❌ Author cannot be empty!");
            return null;
        }

        LocalDate publishDate = getDateFromUser("publish date", title);
        if (publishDate == null) return null;

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter ISBN: ");
        String isbn = scanner.nextLine().trim();

        System.out.print("Enter genre: ");
        String genre = scanner.nextLine().trim();

        int pageCount = getPositiveIntegerFromUser("page count", title);
        if (pageCount <= 0) return null;

        Book book = new Book(null, title, author, status, publishDate, isbn, genre, pageCount, null);
        logger.info("Created book request - Title: '{}', Author: '{}'", title, author);
        return new LibraryRequest(RequestType.CREATE, book);
    }

    private LibraryRequest createAddMagazineRequest() {
        logger.info("Start creating addMagazine request");
        System.out.println("\n📰 === ADD NEW MAGAZINE ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Magazine creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return null;
        }

        System.out.print("Enter editor: ");
        String editor = scanner.nextLine().trim();
        if (editor.isEmpty()) {
            logger.warn("Magazine creation failed for title '{}': Empty editor provided", title);
            System.out.println("❌ Editor cannot be empty!");
            return null;
        }

        LocalDate publishDate = getDateFromUser("publish date", title);
        if (publishDate == null) return null;

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter issue number: ");
        String issueNumber = scanner.nextLine().trim();

        System.out.print("Enter publisher: ");
        String publisher = scanner.nextLine().trim();

        System.out.print("Enter category: ");
        String category = scanner.nextLine().trim();

        Magazine magazine = new Magazine(null, title, editor, status, publishDate, issueNumber, publisher, category, null);
        logger.info("Created magazine request - Title: '{}', Editor: '{}'", title, editor);
        return new LibraryRequest(RequestType.CREATE, magazine);
    }

    private LibraryRequest createAddReferenceRequest() {
        logger.info("Start creating addReference request");
        System.out.println("\n📚 === ADD NEW REFERENCE ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Reference creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return null;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            logger.warn("Reference creation failed for title '{}': Empty author provided", title);
            System.out.println("❌ Author cannot be empty!");
            return null;
        }

        LocalDate publishDate = getDateFromUser("publish date", title);
        if (publishDate == null) return null;

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter reference type (e.g., Dictionary, Encyclopedia): ");
        String referenceType = scanner.nextLine().trim();

        System.out.print("Enter edition: ");
        String edition = scanner.nextLine().trim();

        System.out.print("Enter subject: ");
        String subject = scanner.nextLine().trim();

        Reference reference = new Reference(null, title, author, status, publishDate, referenceType, edition, subject, null);
        logger.info("Created reference request - Title: '{}', Author: '{}'", title, author);
        return new LibraryRequest(RequestType.CREATE, reference);
    }

    private LibraryRequest createAddThesisRequest() {
        logger.info("Start creating addThesis request");
        System.out.println("\n🎓 === ADD NEW THESIS ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            logger.warn("Thesis creation failed: Empty title provided");
            System.out.println("❌ Title cannot be empty!");
            return null;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            logger.warn("Thesis creation failed for title '{}': Empty author provided", title);
            System.out.println("❌ Author cannot be empty!");
            return null;
        }

        LocalDate publishDate = getDateFromUser("publish date", title);
        if (publishDate == null) return null;

        LibraryItemStatus status = LibraryItemStatus.EXIST;

        System.out.print("Enter university: ");
        String university = scanner.nextLine().trim();

        System.out.print("Enter department: ");
        String department = scanner.nextLine().trim();

        System.out.print("Enter advisor: ");
        String advisor = scanner.nextLine().trim();

        Thesis thesis = new Thesis(null, title, author, status, publishDate, university, department, advisor, null);
        logger.info("Created thesis request - Title: '{}', Author: '{}'", title, author);
        return new LibraryRequest(RequestType.CREATE, thesis);
    }

    private LocalDate getDateFromUser(String dateType, String title) {
        LocalDate date = null;
        while (date == null) {
            System.out.print("Enter " + dateType + " (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                date = LocalDate.parse(dateInput);
                logger.debug("Parsed {} for '{}': {}", dateType, title, date);
            } catch (DateTimeParseException e) {
                logger.warn("Invalid date format provided for {} '{}': {}", dateType, title, dateInput);
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
                System.out.print("Do you want to try again? (y/n): ");
                String retry = scanner.nextLine().trim().toLowerCase();
                if (!retry.equals("y") && !retry.equals("yes")) {
                    return null;
                }
            }
        }
        return date;
    }

    private int getPositiveIntegerFromUser(String fieldName, String title) {
        int value = 0;
        while (value <= 0) {
            System.out.print("Enter " + fieldName + " (must be positive): ");
            try {
                value = Integer.parseInt(scanner.nextLine().trim());
                if (value <= 0) {
                    logger.warn("Invalid {} for '{}': {}", fieldName, title, value);
                    System.out.println("❌ " + fieldName + " must be positive!");
                    System.out.print("Do you want to try again? (y/n): ");
                    String retry = scanner.nextLine().trim().toLowerCase();
                    if (!retry.equals("y") && !retry.equals("yes")) {
                        return -1;
                    }
                }
            } catch (NumberFormatException e) {
                logger.warn("Invalid number format for {} of '{}'", fieldName, title);
                System.out.println("❌ Please enter a valid number!");
                System.out.print("Do you want to try again? (y/n): ");
                String retry = scanner.nextLine().trim().toLowerCase();
                if (!retry.equals("y") && !retry.equals("yes")) {
                    return -1;
                }
            }
        }
        return value;
    }

    private LibraryRequest removeLibraryItem() {
        logger.info("Starting removeLibraryItem process");
        System.out.println("\n🗑️ === REMOVE LIBRARY ITEM ===");

        if (library.getLibraryItems().isEmpty()) {
            logger.warn("Remove operation failed: No items in library");
            System.out.println("❌ No items in library!");
            return null;
        }

        System.out.print("Enter item title to remove: ");
        String title = scanner.nextLine().trim();
        logger.info("User searching for item to remove with title: '{}'", title);

        Vector<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            logger.warn("No items found for removal with title containing: '{}'", title);
            System.out.println("❌ No items found with title containing: " + title);
            return null;
        }

        logger.info("Found {} items matching title '{}'", foundItems.size(), title);
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
                return null;
            }

            if (choice < 1 || choice > foundItems.size()) {
                logger.warn("Invalid choice for removal: {} (valid range: 1-{})", choice, foundItems.size());
                System.out.println("❌ Invalid choice!");
                return null;
            }

            int itemId = idByIndex.get(choice);
            LibraryItem itemToRemove = library.getLibraryItemById(itemId);

            if (itemToRemove != null) {
                if (itemToRemove.getStatus() == LibraryItemStatus.BORROWED) {
                    logger.warn("Attempt to remove borrowed item - Type: {}, Title: '{}', ID: {}",
                            itemToRemove.getClass().getSimpleName(), itemToRemove.getTitle(), itemToRemove.getId());
                    System.out.println("❌ Cannot remove borrowed item!");
                    System.out.println("📖 Item is currently borrowed. Please ensure it's returned first.");
                    return null;
                }

                System.out.print("Are you sure you want to remove this item? (y/n): ");
                String confirmation = scanner.nextLine().trim().toLowerCase();
                if (!confirmation.equals("y") && !confirmation.equals("yes")) {
                    logger.info("User cancelled removal after confirmation");
                    System.out.println("❌ Removal cancelled.");
                    return null;
                }

                logger.info("Created remove request - Item ID: {}, Title: '{}'", itemId, itemToRemove.getTitle());
                return new LibraryRequest(RequestType.DELETE, itemId);
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception during remove operation", e);
            System.out.println("❌ Please enter a valid number!");
        }
        return null;
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

        Vector<LibraryItem> results = library.search(keyword);
        logger.info("Search completed for '{}': found {} results", keyword, results.size());
        displayLibraryItems(results, "Search Results for: '" + keyword + "'");
    }

    private void listAllLibraryItems() {
        logger.info("Listing all library items");
        System.out.println("\n📖 === ALL LIBRARY ITEMS ===");
        int itemCount = library.getLibraryItems().size();
        logger.info("Displaying all {} library items", itemCount);
        displayLibraryItems(library.getLibraryItems(), "All Items in Library");
    }

    private void sortLibraryItems() {
        logger.info("Starting sortLibraryItems process");
        System.out.println("\n📅 === SORTED LIBRARY ITEMS ===");
        Vector<LibraryItem> sortedItems = library.sortLibraryItems();
        logger.info("Sorting completed: {} items sorted by publication date", sortedItems.size());
        displayLibraryItems(sortedItems, "Items Sorted by Publication Date (Newest First)");
    }

    private LibraryRequest updateLibraryItemStatus() {
        logger.info("Starting updateLibraryItemStatus process");
        System.out.println("\n✏️ === UPDATE LIBRARY ITEM STATUS ===");

        if (library.getLibraryItems().isEmpty()) {
            logger.warn("Status update failed: No items in library");
            System.out.println("❌ No items in library!");
            return null;
        }

        System.out.print("Enter item title to update: ");
        String title = scanner.nextLine().trim();
        logger.info("User searching for item to update with title: '{}'", title);

        Vector<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            logger.warn("No items found for status update with title containing: '{}'", title);
            System.out.println("❌ No items found with title containing: " + title);
            return null;
        }

        logger.info("Found {} items for status update matching title '{}'", foundItems.size(), title);
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
                return null;
            }

            if (choice < 1 || choice > foundItems.size()) {
                logger.warn("Invalid choice for status update: {} (valid range: 1-{})", choice, foundItems.size());
                System.out.println("❌ Invalid choice!");
                return null;
            }

            int itemId = idByIndex.get(choice);
            LibraryItem itemToUpdate = library.getLibraryItemById(itemId);

            if (itemToUpdate != null) {
                LibraryItemStatus newStatus = null;
                while (newStatus == null) {
                    System.out.print("Enter new status (EXIST/BANNED): ");
                    String statusInput = scanner.nextLine().trim().toUpperCase();
                    try {
                        newStatus = LibraryItemStatus.valueOf(statusInput);
                        if (newStatus == LibraryItemStatus.BORROWED) {
                            newStatus = null;
                            System.out.println("You can not change status to BORROWED!\nPlease select menu option 7");
                            logger.warn("User can not change status of library item to BORROWED directly");
                        }
                    } catch (IllegalArgumentException e) {
                        logger.warn("Invalid status provided for update: {}", statusInput);
                        System.out.println("❌ Invalid status! Please choose from EXIST or BANNED");
                    }
                }

                logger.info("Created status update request - Item ID: {}, New Status: {}", itemId, newStatus);
                return new LibraryRequest(RequestType.UPDATE_STATUS, itemId, newStatus.toString());
            }
        } catch (NumberFormatException e) {
            logger.error("Number format exception during status update operation", e);
            System.out.println("❌ Please enter a valid number!");
        }
        return null;
    }

    private LibraryRequest borrowLibraryItem() {
        logger.info("Starting borrowLibraryItem process");
        System.out.println("\n📥 === BORROW LIBRARY ITEM ===");

        if (library.getLibraryItems().isEmpty()) {
            logger.warn("Borrow operation failed: No items in library");
            System.out.println("❌ No items in library!");
            return null;
        }

        System.out.print("Enter item title to borrow: ");
        String title = scanner.nextLine().trim();
        logger.info("User searching for item to borrow with title: '{}'", title);

        Vector<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            logger.warn("No items found for borrowing with title containing: '{}'", title);
            System.out.println("❌ No items found with title containing: " + title);
            return null;
        }

        Vector<LibraryItem> availableItems = new Vector<>();
        for (LibraryItem item : foundItems) {
            if (item.getStatus() == LibraryItemStatus.EXIST) {
                availableItems.add(item);
            }
        }

        if (availableItems.isEmpty()) {
            logger.warn("No available items found for borrowing with title: '{}'", title);
            System.out.println("❌ No available items found with title: " + title);
            System.out.println("All matching items are either borrowed or banned.");
            return null;
        }

        logger.info("Found {} available items matching title '{}'", availableItems.size(), title);
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
                return null;
            }

            if (choice < 1 || choice > availableItems.size()) {
                logger.warn("Invalid choice for borrowing: {} (valid range: 1-{})", choice, availableItems.size());
                System.out.println("❌ Invalid choice!");
                return null;
            }

            int itemId = idByIndex.get(choice);
            LocalDate returnDate = LocalDate.now().plusDays(14);

            logger.info("Created borrow request - Item ID: {}, Return Date: {}", itemId, returnDate);
            return new LibraryRequest(RequestType.BORROW, itemId, returnDate);
        } catch (NumberFormatException e) {
            logger.error("Number format exception during borrow operation", e);
            System.out.println("❌ Please enter a valid number!");
            return null;
        }
    }

    private LibraryRequest returnLibraryItem() {
        logger.info("Starting returnLibraryItem process");
        System.out.println("\n📤 === RETURN LIBRARY ITEM ===");

        Vector<LibraryItem> borrowedItems = library.getBorrowedItems();
        if (borrowedItems.isEmpty()) {
            logger.warn("Return operation failed: No borrowed items");
            System.out.println("❌ No borrowed items to return!");
            return null;
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
                return null;
            }

            if (choice < 1 || choice > borrowedItems.size()) {
                logger.warn("Invalid choice for return: {} (valid range: 1-{})", choice, borrowedItems.size());
                System.out.println("❌ Invalid choice!");
                return null;
            }

            int itemId = idByIndex.get(choice);
            logger.info("Created return request - Item ID: {}", itemId);
            return new LibraryRequest(RequestType.RETURN, itemId);
        } catch (NumberFormatException e) {
            logger.error("Number format exception during return operation", e);
            System.out.println("❌ Please enter a valid number!");
            return null;
        }
    }

    private void listBorrowedItems() {
        logger.info("Listing borrowed items");
        System.out.println("\n📋 === BORROWED LIBRARY ITEMS ===");

        Vector<LibraryItem> borrowedItems = library.getBorrowedItems();
        if (borrowedItems.isEmpty()) {
            System.out.println("No borrowed items found.");
            return;
        }

        int count = 0;
        synchronized (borrowedItems) {
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
        }
        System.out.println("--- Total: " + count + " borrowed items ---");
    }

    private void displayStatistics() {
        logger.info("Displaying library statistics");
        System.out.println("\n📊 === LIBRARY STATISTICS ===");

        int totalItems = library.getLibraryItems().size();
        int existCount = 0;
        int borrowedCount = 0;
        int bannedCount = 0;
        int overdueCount = 0;
        Vector<LibraryItem> libraryItems = library.getLibraryItems();
        synchronized (libraryItems) {
            for (LibraryItem item : libraryItems) {
                switch (item.getStatus()) {
                    case EXIST:
                        existCount++;
                        break;
                    case BORROWED:
                        borrowedCount++;
                        LocalDate expectedReturn = item.getReturnDate();
                        if (expectedReturn != null && expectedReturn.isBefore(LocalDate.now())) {
                            overdueCount++;
                        }
                        break;
                    case BANNED:
                        bannedCount++;
                        break;
                }
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

    private void displayLibraryItems(Vector<LibraryItem> items, String title) {
        System.out.println("\n=== " + title + " ===");
        int count = 0;
        synchronized (items) {
            for (LibraryItem item : items) {
                System.out.print((++count) + ". ");
                item.display();
            }
        }

        if (count == 0) {
            System.out.println("No items found.");
        } else {
            System.out.println("--- Total: " + count + " items ---");
        }
    }

    public LibraryResult processRequest(LibraryRequest request, Library library) {
        try {
            switch (request.getRequestType()) {
                case CREATE:
                    library.addLibraryItem(request.getItem());
                    return new LibraryResult(true,
                            String.format("%s '%s' created successfully",
                                    request.getItem().getClass().getSimpleName(),
                                    request.getItem().getTitle()));

                case DELETE:
                    LibraryItem itemToDelete = library.getLibraryItemById(request.getItemId());
                    if (itemToDelete != null) {
                        library.removeLibraryItem(itemToDelete);
                        return new LibraryResult(true,
                                String.format("Item '%s' deleted successfully", itemToDelete.getTitle()));
                    }
                    return new LibraryResult(false, "Item not found");

                case BORROW:
                    boolean borrowSuccess = library.borrowItem(request.getItemId(), request.getReturnDate());
                    LibraryItem borrowedItem = library.getLibraryItemById(request.getItemId());
                    if (borrowSuccess) {
                        return new LibraryResult(true,
                                String.format("Item '%s' borrowed successfully. Due: %s",
                                        borrowedItem.getTitle(), request.getReturnDate()));
                    }
                    return new LibraryResult(false,
                            String.format("Failed to borrow item '%s'", borrowedItem.getTitle()));

                case RETURN:
                    boolean returnSuccess = library.returnItem(request.getItemId());
                    LibraryItem returnedItem = library.getLibraryItemById(request.getItemId());
                    if (returnSuccess) {
                        return new LibraryResult(true,
                                String.format("Item '%s' returned successfully", returnedItem.getTitle()));
                    }
                    return new LibraryResult(false,
                            String.format("Failed to return item '%s'", returnedItem.getTitle()));

                case UPDATE_STATUS:
                    LibraryItem itemToUpdate = library.getLibraryItemById(request.getItemId());
                    if (itemToUpdate != null) {
                        LibraryItemStatus newStatus = LibraryItemStatus.valueOf(request.getNewStatus());
                        LibraryItemStatus oldStatus = itemToUpdate.getStatus();
                        itemToUpdate.setStatus(newStatus);
                        return new LibraryResult(true,
                                String.format("Item '%s' status changed from %s to %s",
                                        itemToUpdate.getTitle(), oldStatus, newStatus));
                    }
                    return new LibraryResult(false, "Item not found");

                default:
                    return new LibraryResult(false, "Unknown request type");
            }
        } catch (Exception e) {
            return new LibraryResult(false, "Error processing request: " + e.getMessage());
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