//package test.controller;
//
//import controller.CommandLineController;
//import model.Book;
//import model.Library;
//import model.data_structures.GenericLinkedList;
//import model.enums.LibraryItemStatus;
//import org.junit.jupiter.api.AfterEach;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.io.ByteArrayInputStream;
//import java.io.InputStream;
//import java.time.LocalDate;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.any;
//import static org.mockito.ArgumentMatchers.anyString;
//import static org.mockito.Mockito.*;
//
//@ExtendWith(MockitoExtension.class)
//class CommandLineControllerTest {
//
//    @Mock
//    private Library mockLibrary;
//
//    private CommandLineController controller;
//    private GenericLinkedList<Book> mockBookList;
//    private InputStream originalSystemIn;
//
//    @BeforeEach
//    void setUp() {
//        controller = new CommandLineController(mockLibrary);
//        mockBookList = new GenericLinkedList<>();
//        originalSystemIn = System.in;
//    }
//
//    @AfterEach
//    void tearDown() {
//        System.setIn(originalSystemIn);
//    }
//
//    private void setInput(String input) {
//        InputStream in = new ByteArrayInputStream(input.getBytes());
//        System.setIn(in);
//        // Recreate controller to use new input stream
//        controller = new CommandLineController(mockLibrary);
//    }
//
//    @Test
//    void testHandleChoiceExit() {
//        assertTrue(controller.handleChoice("0"));
//    }
//
//    @Test
//    void testHandleChoiceInvalidOption() {
//        assertFalse(controller.handleChoice("99"));
//        assertFalse(controller.handleChoice("invalid"));
//        assertFalse(controller.handleChoice(""));
//    }
//
//    @Test
//    void testHandleChoiceAddBookSuccess() {
//        String input = "Valid Title\nValid Author\n2023-01-01\nEXIST\n";
//        setInput(input);
//
//        assertFalse(controller.handleChoice("1"));
//        // Verify addBook was called - you might need to add verification if Library.addBook is mocked
//    }
//
//    @Test
//    void testHandleChoiceListBooks() {
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList);
//
//        assertFalse(controller.handleChoice("4"));
//        verify(mockLibrary, atLeastOnce()).getLibraryItems();
//    }
//
//    @Test
//    void testHandleChoiceSearchBooks() {
//        String input = "java\n";
//        setInput(input);
//
//        GenericLinkedList<Book> searchResults = new GenericLinkedList<>();
//        when(mockLibrary.search("java")).thenReturn(searchResults);
//
//        assertFalse(controller.handleChoice("3"));
//        verify(mockLibrary, times(1)).search("java");
//    }
//
//    @Test
//    void testHandleChoiceSearchBooksEmptyKeyword() {
//        String input = "\n"; // Empty input
//        setInput(input);
//
//        assertFalse(controller.handleChoice("3"));
//        // Should not call search with empty keyword
//        verify(mockLibrary, never()).search(anyString());
//    }
//
//    @Test
//    void testHandleChoiceSortBooks() {
//        GenericLinkedList<Book> sortedResults = new GenericLinkedList<>();
//        when(mockLibrary.sortLibraryItems()).thenReturn(sortedResults);
//
//        assertFalse(controller.handleChoice("5"));
//        verify(mockLibrary, times(1)).sortLibraryItems();
//    }
//
//    @Test
//    void testGetUserInput() {
//        String testInput = "test input\n";
//        setInput(testInput);
//
//        // Create new controller to use the set input stream
//        CommandLineController freshController = new CommandLineController(mockLibrary);
//        assertEquals("test input", freshController.getUserInput());
//    }
//
//    @Test
//    void testExitProgram() {
//        doNothing().when(mockLibrary).writeToFile();
//
//        assertDoesNotThrow(() -> controller.exitProgram());
//        verify(mockLibrary, times(1)).writeToFile();
//    }
//
//    @Test
//    void testPrintMenu() {
//        assertDoesNotThrow(() -> controller.printMenu());
//    }
//
//    @Test
//    void testHandleChoiceUpdateBookStatus() {
//        Book testBook = new Book("Test Book", "Test Author", LibraryItemStatus.EXIST, LocalDate.now());
//        mockBookList.add(testBook);
//
//        String input = "Test Book\n1\nBORROWED\n";
//        setInput(input);
//
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList);
//        when(mockLibrary.search("Test Book")).thenReturn(mockBookList);
//
//        assertFalse(controller.handleChoice("6"));
//        assertEquals(LibraryItemStatus.BORROWED, testBook.getStatus());
//    }
//
//    @Test
//    void testHandleChoiceUpdateBookStatusEmptyLibrary() {
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList); // Empty list
//
//        assertFalse(controller.handleChoice("6"));
//        verify(mockLibrary, never()).search(anyString());
//    }
//
//    @Test
//    void testHandleChoiceDisplayStatistics() {
//        // Add some books with different statuses
//        Book book1 = new Book("Book1", "Author1", LibraryItemStatus.EXIST, LocalDate.now());
//        Book book2 = new Book("Book2", "Author2", LibraryItemStatus.BORROWED, LocalDate.now());
//        Book book3 = new Book("Book3", "Author3", LibraryItemStatus.BANNED, LocalDate.now());
//        mockBookList.add(book1);
//        mockBookList.add(book2);
//        mockBookList.add(book3);
//
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList);
//
//        assertFalse(controller.handleChoice("7"));
//        // Use atLeastOnce instead of exact count to be more flexible
//        verify(mockLibrary, atLeastOnce()).getLibraryItems();
//    }
//
//    @Test
//    void testHandleChoiceRemoveBook() {
//        Book testBook = new Book("Test Book", "Test Author", LibraryItemStatus.EXIST, LocalDate.now());
//        mockBookList.add(testBook);
//
//        String input = "Test Book\n1\n";
//        setInput(input);
//
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList);
//        when(mockLibrary.search("Test Book")).thenReturn(mockBookList);
//
//        assertFalse(controller.handleChoice("2"));
//        verify(mockLibrary, times(1)).removeLibraryItem(testBook);
//    }
//
//    @Test
//    void testHandleChoiceRemoveBookEmptyLibrary() {
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList); // Empty list
//
//        assertFalse(controller.handleChoice("2"));
//        verify(mockLibrary, never()).search(anyString());
//    }
//
//    @Test
//    void testHandleChoiceRemoveBookCancel() {
//        Book testBook = new Book("Test Book", "Test Author", LibraryItemStatus.EXIST, LocalDate.now());
//        mockBookList.add(testBook);
//
//        String input = "Test Book\n0\n"; // Cancel operation
//        setInput(input);
//
//        when(mockLibrary.getLibraryItems()).thenReturn(mockBookList);
//        when(mockLibrary.search("Test Book")).thenReturn(mockBookList);
//
//        assertFalse(controller.handleChoice("2"));
//        verify(mockLibrary, never()).removeLibraryItem(any(Book.class));
//    }
//}