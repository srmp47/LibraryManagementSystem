package library.models;

import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.io.File;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryTest {

    private Library library;
    private Book book;
    private Magazine magazine;
    private static final String TEST_DATA_FILE = "test_library_data.json";

    @BeforeEach
    void setUp() {
        // Use test-specific data file to avoid conflicts with production data
        library = new Library(TEST_DATA_FILE);
        book = TestDataFactory.createSampleBook(1);
        magazine = TestDataFactory.createSampleMagazine();

        // Clear any existing test data
        clearLibraryItems();
    }

    @AfterEach
    void tearDown() {
        // Clean up test data file after each test
        File testFile = new File(TEST_DATA_FILE);
        if (testFile.exists()) {
            testFile.delete();
        }
    }

    private void clearLibraryItems() {
        // Remove all items from library to ensure clean state
        library.getLibraryItems().toList().forEach(library::removeLibraryItem);
    }

    @Test
    void testLibraryInitialization() {
        assertNotNull(library);
        assertNotNull(library.getLibraryItems());
    }

    @Test
    void testAddLibraryItem() {
        int initialSize = library.getLibraryItems().getSize();

        library.addLibraryItem(book);

        assertEquals(initialSize + 1, library.getLibraryItems().getSize());
        assertTrue(library.getLibraryItems().toList().contains(book));
    }

    @Test
    void testRemoveLibraryItem() {
        library.addLibraryItem(book);
        int sizeAfterAdd = library.getLibraryItems().getSize();

        library.removeLibraryItem(book);

        assertEquals(sizeAfterAdd - 1, library.getLibraryItems().getSize());
        assertFalse(library.getLibraryItems().toList().contains(book));
    }

    @Test
    void testSearchLibraryItems() {
        // Create books with specific titles and authors for testing
        Book greatGatsby = new Book(
                100,
                "The Great Gatsby",
                "F. Scott Fitzgerald",
                LibraryItemStatus.EXIST,
                LocalDate.of(1925, 4, 10),
                "978-0-7432-7356-5",
                "Fiction",
                180
        );

        Book testBook = new Book(
                101,
                "Test Book",
                "Test Author",
                LibraryItemStatus.EXIST,
                LocalDate.now(),
                "1234567890",
                "Test Genre",
                200
        );

        library.addLibraryItem(greatGatsby);
        library.addLibraryItem(testBook);
        library.addLibraryItem(magazine);

        // Search by title
        GenericLinkedList<LibraryItem> titleResults = library.search("Great Gatsby");
        assertEquals(1, titleResults.getSize());
        assertEquals(greatGatsby.getTitle(), titleResults.toList().get(0).getTitle());

        // Search by author
        GenericLinkedList<LibraryItem> authorResults = library.search("F. Scott Fitzgerald");
        assertEquals(1, authorResults.getSize());
        assertEquals(greatGatsby.getAuthor(), authorResults.toList().get(0).getAuthor());

        // Search with no results
        GenericLinkedList<LibraryItem> noResults = library.search("Nonexistent");
        assertEquals(0, noResults.getSize());
    }

    @ParameterizedTest
    @CsvSource({
            "test book, 1",
            "TEST BOOK, 1",
            "Test Book, 1",
            "author 1, 1",
            "AUTHOR 1, 1",
            "nonexistent, 0"
    })
    void testSearchCaseInsensitive(String searchQuery, int expectedCount) {
        library.addLibraryItem(book); // This book has title "Test Book 1" and author "Author 1"

        GenericLinkedList<LibraryItem> results = library.search(searchQuery);
        assertEquals(expectedCount, results.getSize(),
                "Search for '" + searchQuery + "' should return " + expectedCount + " results");
    }

    @Test
    void testSortLibraryItems() {
        Book oldBook = new Book(
                10,
                "Old Book",
                "Old Author",
                LibraryItemStatus.EXIST,
                LocalDate.now().minusYears(5),
                "ISBN-OLD",
                "Genre",
                300
        );

        Book newBook = new Book(
                11,
                "New Book",
                "New Author",
                LibraryItemStatus.EXIST,
                LocalDate.now(),
                "ISBN-NEW",
                "Genre",
                200
        );

        library.addLibraryItem(oldBook);
        library.addLibraryItem(newBook);

        GenericLinkedList<LibraryItem> sortedItems = library.sortLibraryItems();

        // The newest item should be first (reverse chronological order)
        assertEquals(newBook.getTitle(), sortedItems.toList().get(0).getTitle());
        assertEquals(oldBook.getTitle(), sortedItems.toList().get(1).getTitle());
    }

    @Test
    void testFileOperations() {
        // Test writing to file
        library.addLibraryItem(book);
        library.addLibraryItem(magazine);

        assertDoesNotThrow(() -> library.writeToFile());

        // Verify file was created
        File testFile = new File(TEST_DATA_FILE);
        assertTrue(testFile.exists());

        // Test that new library can read the file
        Library newLibrary = new Library(TEST_DATA_FILE);
        assertEquals(2, newLibrary.getLibraryItems().getSize());
    }

    @Test
    void testEmptyLibraryOperations() {
        Library emptyLibrary = new Library("empty_test_data.json");

        // Clear any items that might have been loaded
        emptyLibrary.getLibraryItems().toList().forEach(emptyLibrary::removeLibraryItem);

        assertDoesNotThrow(() -> emptyLibrary.sortLibraryItems());
        assertDoesNotThrow(() -> emptyLibrary.search("test"));
        assertDoesNotThrow(() -> emptyLibrary.writeToFile());

        GenericLinkedList<LibraryItem> emptySearch = emptyLibrary.search("anything");
        assertEquals(0, emptySearch.getSize());

        // Clean up
        new File("empty_test_data.json").delete();
    }

    @Test
    void testSearchPartialMatches() {
        library.addLibraryItem(book); // Title: "Test Book 1"
        library.addLibraryItem(magazine);

        // Test partial title matching
        GenericLinkedList<LibraryItem> partialResults = library.search("Test");
        assertEquals(1, partialResults.getSize());

        GenericLinkedList<LibraryItem> partialResults2 = library.search("Book");
        assertEquals(1, partialResults2.getSize());

        GenericLinkedList<LibraryItem> authorPartial = library.search("Author");
        assertEquals(1, authorPartial.getSize());
    }
}