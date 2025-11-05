package controller;
import model.Book;
import model.Library;
import model.data_structures.GenericLinkedList;
import model.enums.BookStatus;

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
    public CommandLineController(Library library) {
        this.library = library;
        this.scanner = new Scanner(System.in);
    }

    public void printMenu() {
        System.out.println("\n📚 === MAIN MENU ===");
        System.out.println("1. ➕ Add Book");
        System.out.println("2. 🗑️ Remove Book");
        System.out.println("3. 🔍 Search Books");
        System.out.println("4. 📖 List All Books");
        System.out.println("5. 📅 Sort Books by Publication Date");
        System.out.println("6. ✏️ Update Book Status");
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
                addBook();
                return false;
            case "2":
                removeBook();
                return false;
            case "3":
                searchBooks();
                return false;
            case "4":
                listAllBooks();
                return false;
            case "5":
                sortBooks();
                return false;
            case "6":
                updateBookStatus();
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

    private void addBook() {
        System.out.println("\n➕ === ADD NEW BOOK ===");

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

        BookStatus status = null;
        while (status == null) {
            System.out.print("Enter status (EXIST/BORROWED/BANNED): ");
            String statusInput = scanner.nextLine().trim().toUpperCase();
            try {
                status = BookStatus.valueOf(statusInput);
            } catch (IllegalArgumentException e) {
                System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
            }
        }

        Book book = new Book(title, author, status, publishDate);
        library.addBook(book);
        System.out.println("✅ Book added successfully!");
    }

    private void removeBook() {
        System.out.println("\n🗑️ === REMOVE BOOK ===");

        if (library.getBooks().isEmpty()) {
            System.out.println("❌ No books in library!");
            return;
        }

        System.out.print("Enter book title to remove: ");
        String title = scanner.nextLine().trim();

        GenericLinkedList<Book> foundBooks = library.search(title);
        if (foundBooks.isEmpty()) {
            System.out.println("❌ No books found with title containing: " + title);
            return;
        }

        System.out.println("\n📚 Found Books:");
        int index = 1;
        for (Book book : foundBooks) {
            System.out.printf("%d. %s by %s (%s) - Status: %s%n",
                    index++, book.getTitle(), book.getAuthor(),
                    book.getPublishDate(), book.getStatus());
        }

        System.out.print("Enter the number of book to remove (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > foundBooks.getSize()) {
                System.out.println("❌ Invalid choice!");
                return;
            }

            Book bookToRemove = null;
            int currentIndex = 1;
            for (Book book : foundBooks) {
                if (currentIndex == choice) {
                    bookToRemove = book;
                    break;
                }
                currentIndex++;
            }

            if (bookToRemove != null) {
                library.removeBook(bookToRemove);
                System.out.println("✅ Book removed successfully!");
            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void searchBooks() {
        System.out.println("\n🔍 === SEARCH BOOKS ===");

        System.out.print("Enter search keyword (title or author): ");
        String keyword = scanner.nextLine().trim();

        if (keyword.isEmpty()) {
            System.out.println("❌ Search keyword cannot be empty!");
            return;
        }

        GenericLinkedList<Book> results = library.search(keyword);
        displayBooks(results, "Search Results for: '" + keyword + "'");
    }

    private void listAllBooks() {
        System.out.println("\n📖 === ALL BOOKS ===");
        displayBooks(library.getBooks(), "All Books in Library");
    }

    private void sortBooks() {
        System.out.println("\n📅 === SORTED BOOKS ===");
        GenericLinkedList<Book> sortedBooks = library.sortBooks();
        displayBooks(sortedBooks, "Books Sorted by Publication Date (Newest First)");
    }

    private void updateBookStatus() {
        System.out.println("\n✏️ === UPDATE BOOK STATUS ===");

        if (library.getBooks().isEmpty()) {
            System.out.println("❌ No books in library!");
            return;
        }

        System.out.print("Enter book title to update: ");
        String title = scanner.nextLine().trim();

        GenericLinkedList<Book> foundBooks = library.search(title);
        if (foundBooks.isEmpty()) {
            System.out.println("❌ No books found with title containing: " + title);
            return;
        }

        System.out.println("\n📚 Found Books:");
        int index = 1;
        for (Book book : foundBooks) {
            System.out.printf("%d. %s by %s - Current Status: %s%n",
                    index++, book.getTitle(), book.getAuthor(), book.getStatus());
        }

        System.out.print("Enter the number of book to update (0 to cancel): ");
        try {
            int choice = Integer.parseInt(scanner.nextLine().trim());
            if (choice == 0) {
                System.out.println("❌ Operation cancelled.");
                return;
            }

            if (choice < 1 || choice > foundBooks.getSize()) {
                System.out.println("❌ Invalid choice!");
                return;
            }

            Book bookToUpdate = null;
            int currentIndex = 1;
            for (Book book : foundBooks) {
                if (currentIndex == choice) {
                    bookToUpdate = book;
                    break;
                }
                currentIndex++;
            }

            if (bookToUpdate != null) {
                BookStatus newStatus = null;
                while (newStatus == null) {
                    System.out.print("Enter new status (EXIST/BORROWED/BANNED): ");
                    String statusInput = scanner.nextLine().trim().toUpperCase();
                    try {
                        newStatus = BookStatus.valueOf(statusInput);
                    } catch (IllegalArgumentException e) {
                        System.out.println("❌ Invalid status! Please choose from EXIST, BORROWED, or BANNED");
                    }
                }

                bookToUpdate.setStatus(newStatus);
                System.out.println("✅ Book status updated successfully!");

            }
        } catch (NumberFormatException e) {
            System.out.println("❌ Please enter a valid number!");
        }
    }

    private void displayStatistics() {
        System.out.println("\n📊 === LIBRARY STATISTICS ===");

        int totalBooks = library.getBooks().getSize();
        int existCount = 0;
        int borrowedCount = 0;
        int bannedCount = 0;

        for (Book book : library.getBooks()) {
            switch (book.getStatus()) {
                case EXIST: existCount++; break;
                case BORROWED: borrowedCount++; break;
                case BANNED: bannedCount++; break;
            }
        }

        System.out.println("Total Books: " + totalBooks);
        System.out.println("Available (EXIST): " + existCount);
        System.out.println("Borrowed: " + borrowedCount);
        System.out.println("Banned: " + bannedCount);

    }

    private void displayBooks(Iterable<Book> books, String title) {
        System.out.println("\n=== " + title + " ===");
        int count = 0;
        for (Book book : books) {
            String statusEmoji = "";
            switch (book.getStatus()) {
                case EXIST: statusEmoji = "✅"; break;
                case BORROWED: statusEmoji = "📖"; break;
                case BANNED: statusEmoji = "🚫"; break;
            }

            System.out.printf("%d. %s %s by %s (%s)%n",
                    ++count, statusEmoji, book.getTitle(),
                    book.getAuthor(), book.getPublishDate());
        }

        if (count == 0) {
            System.out.println("No books found.");
        } else {
            System.out.println("--- Total: " + count + " books ---");
        }
    }

    public void exitProgram() {
        System.out.println("\n💾 Saving data to file...");
        library.writeToFile();
        System.out.println("👋 Thank you for using Library Management System!");
    }


}