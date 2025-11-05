package test.integration;

import model.Book;
import model.Library;
import model.data_structures.GenericLinkedList;
import model.enums.BookStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class LibraryIntegrationTest {
    private Library library;
    private File testDataFile;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws IOException {
        testDataFile = tempDir.resolve("test_library_data.json").toFile();
        library = new Library();
        if (testDataFile.exists()) {
            testDataFile.delete();
        }
    }

    @AfterEach
    void tearDown() {
        if (testDataFile.exists()) {
            testDataFile.delete();
        }
    }

    @Test
    void testCompleteWorkflow() {
        Book book1 = new Book("Design Patterns", "Gamma", BookStatus.EXIST,
                LocalDate.of(1994, 1, 1));
        Book book2 = new Book("Clean Code", "Martin", BookStatus.BORROWED,
                LocalDate.of(2008, 1, 1));

        library.addBook(book1);
        library.addBook(book2);

        assertEquals(2, library.getBooks().getSize());

        GenericLinkedList<Book> patternResults = library.search("Pattern");
        assertEquals(1, patternResults.getSize());
        assertEquals("Design Patterns", patternResults.toList().get(0).getTitle());

        GenericLinkedList<Book> sorted = library.sortBooks();
        assertEquals("Clean Code", sorted.toList().get(0).getTitle());
        assertEquals("Design Patterns", sorted.toList().get(1).getTitle());

        library.removeBook(book1);
        assertEquals(1, library.getBooks().getSize());

        GenericLinkedList<Book> cleanCodeResults = library.search("Clean");
        assertEquals(1, cleanCodeResults.getSize());
        assertEquals("Clean Code", cleanCodeResults.toList().get(0).getTitle());
    }
}