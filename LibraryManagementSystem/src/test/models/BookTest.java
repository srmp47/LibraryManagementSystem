//package test.model;
//
//import model.Book;
//import model.enums.LibraryItemStatus;
//import org.junit.jupiter.api.Test;
//import java.time.LocalDate;
//import static org.junit.jupiter.api.Assertions.*;
//
//class BookTest {
//
//    @Test
//    void testBookCreation() {
//        LocalDate publishDate = LocalDate.of(2020, 1, 15);
//        Book book = new Book("Test Title", "Test Author", LibraryItemStatus.EXIST, publishDate);
//
//        assertEquals("Test Title", book.getTitle());
//        assertEquals("Test Author", book.getAuthor());
//        assertEquals(LibraryItemStatus.EXIST, book.getStatus());
//        assertEquals(publishDate, book.getPublishDate());
//    }
//
//    @Test
//    void testSetStatus() {
//        LocalDate publishDate = LocalDate.now();
//        Book book = new Book(1,"Title", "Author", LibraryItemStatus.EXIST, publishDate);
//
//        assertEquals(LibraryItemStatus.EXIST, book.getStatus());
//
//        book.setStatus(LibraryItemStatus.BORROWED);
//        assertEquals(LibraryItemStatus.BORROWED, book.getStatus());
//
//        book.setStatus(LibraryItemStatus.BANNED);
//        assertEquals(LibraryItemStatus.BANNED, book.getStatus());
//    }
//
//    @Test
//    void testEqualsAndHashCode() {
//        LocalDate date1 = LocalDate.of(2020, 1, 1);
//        LocalDate date2 = LocalDate.of(2021, 1, 1);
//
//        Book book1 = new Book("Same Title", "Same Author", LibraryItemStatus.EXIST, date1);
//        Book book2 = new Book("Same Title", "Same Author", LibraryItemStatus.BORROWED, date1); // Different status
//        Book book3 = new Book("Same Title", "Same Author", LibraryItemStatus.EXIST, date1); // Same everything
//        Book book4 = new Book("Different Title", "Same Author", LibraryItemStatus.EXIST, date1);
//        Book book5 = new Book("Same Title", "Different Author", LibraryItemStatus.EXIST, date1);
//        Book book6 = new Book("Same Title", "Same Author", LibraryItemStatus.EXIST, date2); // Different date
//
//
//        assertEquals(book1, book3);
//        assertNotEquals(book1, book2);
//        assertNotEquals(book1, book4);
//        assertNotEquals(book1, book5);
//        assertNotEquals(book1, book6);
//
//        assertEquals(book1.hashCode(), book3.hashCode());
//        assertNotEquals(book1.hashCode(), book4.hashCode());
//    }
//
//    @Test
//    void testToString() {
//        LocalDate publishDate = LocalDate.of(2020, 1, 15);
//        Book book = new Book("Test Book", "John Doe", LibraryItemStatus.BORROWED, publishDate);
//
//        String result = book.toString();
//
//        assertTrue(result.contains("Test Book"));
//        assertTrue(result.contains("John Doe"));
//        assertTrue(result.contains("BORROWED"));
//        assertTrue(result.contains("2020-01-15"));
//    }
//}