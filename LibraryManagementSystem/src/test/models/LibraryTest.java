//package test.model;
//
//import model.Book;
//import model.Library;
//import model.data_structures.GenericLinkedList;
//import model.enums.LibraryItemStatus;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDate;
//import static org.junit.jupiter.api.Assertions.*;
//
//class LibraryTest {
//    private Library library;
//    private Book testBook1;
//    private Book testBook2;
//
//    @BeforeEach
//    void setUp() {
//        library = new Library();
//        testBook1 = new Book("Java Programming", "John Smith", LibraryItemStatus.EXIST,
//                LocalDate.of(2020, 5, 15));
//        testBook2 = new Book("Python Basics", "Jane Doe", LibraryItemStatus.BORROWED,
//                LocalDate.of(2019, 3, 10));
//    }
//
//    @Test
//    void testAddBook() {
//        assertEquals(0, library.getLibraryItems().getSize());
//
//        library.addLibraryItem(testBook1);
//        assertEquals(1, library.getLibraryItems().getSize());
//
//        library.addLibraryItem(testBook2);
//        assertEquals(2, library.getLibraryItems().getSize());
//    }
//
//    @Test
//    void testRemoveBook() {
//        library.addLibraryItem(testBook1);
//        library.addLibraryItem(testBook2);
//        assertEquals(2, library.getLibraryItems().getSize());
//
//        library.removeLibraryItem(testBook1);
//        assertEquals(1, library.getLibraryItems().getSize());
//
//        library.removeLibraryItem(testBook1);
//        assertEquals(1, library.getLibraryItems().getSize()); // Size should remain same
//    }
//
//    @Test
//    void testSearch() {
//        library.addLibraryItem(testBook1);
//        library.addLibraryItem(testBook2);
//
//        GenericLinkedList<Book> results1 = library.search("Java");
//        assertEquals(1, results1.getSize());
//        assertEquals("Java Programming", results1.toList().get(0).getTitle());
//
//        GenericLinkedList<Book> results2 = library.search("Doe");
//        assertEquals(1, results2.getSize());
//        assertEquals("Python Basics", results2.toList().get(0).getTitle());
//
//        GenericLinkedList<Book> results3 = library.search("Nonexistent");
//        assertTrue(results3.isEmpty());
//
//        GenericLinkedList<Book> results4 = library.search("java");
//        assertEquals(1, results4.getSize());
//    }
//
//    @Test
//    void testSortBooks() {
//        Book olderBook = new Book("Older Book", "Author A", LibraryItemStatus.EXIST,
//                LocalDate.of(2018, 1, 1));
//        Book newerBook = new Book("Newer Book", "Author B", LibraryItemStatus.EXIST,
//                LocalDate.of(2021, 1, 1));
//        Book middleBook = new Book("Middle Book", "Author C", LibraryItemStatus.EXIST,
//                LocalDate.of(2020, 1, 1));
//
//        library.addLibraryItem(olderBook);
//        library.addLibraryItem(newerBook);
//        library.addLibraryItem(middleBook);
//
//        GenericLinkedList<Book> sorted = library.sortLibraryItems();
//
//        assertEquals("Newer Book", sorted.toList().get(0).getTitle());
//        assertEquals("Middle Book", sorted.toList().get(1).getTitle());
//        assertEquals("Older Book", sorted.toList().get(2).getTitle());
//    }
//
//    @Test
//    void testEmptyLibrary() {
//        assertTrue(library.getLibraryItems().isEmpty());
//
//        GenericLinkedList<Book> searchResults = library.search("anything");
//        assertTrue(searchResults.isEmpty());
//
//        GenericLinkedList<Book> sortedResults = library.sortLibraryItems();
//        assertTrue(sortedResults.isEmpty());
//    }
//}