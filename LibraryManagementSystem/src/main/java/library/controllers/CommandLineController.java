package library.controllers;

import library.models.*;
import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Scanner;

public class CommandLineController {
    private final Library library;
    private final Scanner scanner;

    public CommandLineController() {
        this.library = new Library();
        this.scanner = new Scanner(System.in);
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
                return true;
            default:
                System.out.println("❌ Invalid choice! Please try again.");
                return false;
        }
    }

    private void addLibraryItem() {
        System.out.println("\n➕ === ADD NEW LIBRARY ITEM ===");

        System.out.println("Choose item type:");
        System.out.println("1. Book");
        System.out.println("2. Magazine");
        System.out.println("3. Reference");
        System.out.println("4. Thesis");
        System.out.print("Enter your choice: ");

        String typeChoice = scanner.nextLine().trim();
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
                System.out.println("❌ Invalid type choice!");
                return;
        }
    }

    private void addBook() {
        System.out.println("\n📘 === ADD NEW BOOK ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
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
                    System.out.println("❌ Page count must be positive!");
                }
            } catch (NumberFormatException e) {
                System.out.println("❌ Please enter a valid number!");
            }
        }

        Book book = new Book(null ,title, author, status, publishDate, isbn, genre, pageCount);
        library.addLibraryItem(book);
        System.out.println("✅ Book added successfully!");
    }

    private void addMagazine() {
        System.out.println("\n📰 === ADD NEW MAGAZINE ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter editor: ");
        String editor = scanner.nextLine().trim();
        if (editor.isEmpty()) {
            System.out.println("❌ Editor cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
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
        System.out.println("✅ Magazine added successfully!");
    }

    private void addReference() {
        System.out.println("\n📚 === ADD NEW REFERENCE ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
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
        System.out.println("✅ Reference added successfully!");
    }

    private void addThesis() {
        System.out.println("\n🎓 === ADD NEW THESIS ===");

        System.out.print("Enter title: ");
        String title = scanner.nextLine().trim();
        if (title.isEmpty()) {
            System.out.println("❌ Title cannot be empty!");
            return;
        }

        System.out.print("Enter author: ");
        String author = scanner.nextLine().trim();
        if (author.isEmpty()) {
            System.out.println("❌ Author cannot be empty!");
            return;
        }

        LocalDate publishDate = null;
        while (publishDate == null) {
            System.out.print("Enter publish date (YYYY-MM-DD): ");
            String dateInput = scanner.nextLine().trim();
            try {
                publishDate = LocalDate.parse(dateInput);
            } catch (DateTimeParseException e) {
                System.out.println("❌ Invalid date format! Please use YYYY-MM-DD");
            }
        }

        LibraryItemStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = LibraryItemStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
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
        System.out.println("✅ Thesis added successfully!");
    }

    private void removeLibraryItem() {
        System.out.println("\n🗑️ === REMOVE LIBRARY ITEM ===");

        if (library.getLibraryItems().isEmpty()) {
            System.out.println("❌ No items in library!");
            return;
        }

        System.out.print("Enter item title to remove: ");
        String title = scanner.nextLine().trim();

        GenericLinkedList<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            System.out.println("❌ No items found with title containing: " + title);
            return;
        }

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
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > foundItems.getSize()) {
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
                System.out.println("✅ Item removed successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void searchLibraryItems() {
        System.out.println("\n🔍 === SEARCH LIBRARY ITEMS ===");

        System.out.print("Enter search keyword (title or author): ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("❌ Search keyword cannot be empty!");
            return;
        }

        GenericLinkedList<LibraryItem> results = library.search(keyword);
        displayLibraryItems(results, "Search Results for: '" + keyword + "'");
    }

    private void listAllLibraryItems() {
        System.out.println("\n📖 === ALL LIBRARY ITEMS ===");
        displayLibraryItems(library.getLibraryItems(), "All Items in Library");
    }

    private void sortLibraryItems() {
        System.out.println("\n📅 === SORTED LIBRARY ITEMS ===");
        GenericLinkedList<LibraryItem> sortedItems = library.sortLibraryItems();
        displayLibraryItems(sortedItems, "Items Sorted by Publication Date (Newest First)");
    }

    private void updateLibraryItemStatus() {
        System.out.println("\n✏️ === UPDATE LIBRARY ITEM STATUS ===");

        if (library.getLibraryItems().isEmpty()) {
            System.out.println("❌ No items in library!");
            return;
        }

        System.out.print("Enter item title to update: ");
        String title = scanner.nextLine().trim();

        GenericLinkedList<LibraryItem> foundItems = library.search(title);
        if (foundItems.isEmpty()) {
            System.out.println("❌ No items found with title containing: " + title);
            return;
        }

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
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > foundItems.getSize()) {
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
                LibraryItemStatus newStatus = null;
                while (newStatus == null) {
                    System.out.print("Enter new status (EXIST/BORROWED/BANNED): ");
                    String statusInput = scanner.nextLine().trim().toUpperCase();
                    try {
                        newStatus = LibraryItemStatus.valueOf(statusInput);
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
                    }
                }

                itemToUpdate.setStatus(newStatus);
                System.out.println("✅ Item status updated successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void displayStatistics() {
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
        System.out.println("\n💾 Saving data to file...");
        library.writeToFile();
        System.out.println("👋 Thank you for using Library Management System!");
    }
}