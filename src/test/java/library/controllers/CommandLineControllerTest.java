package library.controllers;

import library.models.*;
import library.models.data_structures.GenericLinkedList;
import library.models.enums.LibraryItemStatus;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.PrintStream;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class CommandLineControllerTest {

    private CommandLineController controller;
    private Library mockLibrary;
    private ByteArrayOutputStream outputStream;
    private PrintStream originalOut;
    private InputStream originalIn;

    @BeforeEach
    void setUp() {
        mockLibrary = mock(Library.class);
        outputStream = new ByteArrayOutputStream();
        originalOut = System.out;
        originalIn = System.in;
        System.setOut(new PrintStream(outputStream));
    }

    @AfterEach
    void tearDown() {
        System.setOut(originalOut);
        System.setIn(originalIn);
    }

    private void setInput(String input) {
        System.setIn(new ByteArrayInputStream(input.getBytes()));
    }

    @Test
    void testConstructorWithNoParameters() {
        setInput("\n");
        CommandLineController defaultController = new CommandLineController();
        assertNotNull(defaultController.getLibrary());
    }

    @Test
    void testConstructorWithLibraryParameter() {
        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        assertSame(mockLibrary, controller.getLibrary());
    }

    @Test
    void testPrintMenu() {
        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        controller.printMenu();

        String output = outputStream.toString();
        assertTrue(output.contains("MAIN MENU"));
        assertTrue(output.contains("Add Library Item"));
        assertTrue(output.contains("Remove Library Item"));
        assertTrue(output.contains("Search Library Items"));
        assertTrue(output.contains("List All Library Items"));
        assertTrue(output.contains("Sort Library Items"));
        assertTrue(output.contains("Update Library Item Status"));
        assertTrue(output.contains("Display Statistics"));
        assertTrue(output.contains("Exit"));
    }

    @Test
    void testGetUserInput() {
        String testInput = "test input\n";
        setInput(testInput);
        controller = new CommandLineController(mockLibrary);

        String result = controller.getUserInput();
        assertEquals("test input", result);
    }

    @Test
    void testHandleChoiceExit() {
        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        boolean result = controller.handleChoice("0");
        assertTrue(result);
    }

    @Test
    void testHandleChoiceInvalid() {
        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        boolean result = controller.handleChoice("invalid");
        assertFalse(result);

        String output = outputStream.toString();
        assertTrue(output.contains("Invalid choice"));
    }

    @Test
    void testHandleChoiceAddLibraryItem() {
        String input = "1\n1\nTest Book\nTest Author\n2020-01-01\nEXIST\n123-456\nFiction\n100\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        boolean result = controller.handleChoice("1");
        assertFalse(result);
        verify(mockLibrary, times(1)).addLibraryItem(any(Book.class));
    }

    @Test
    void testHandleChoiceRemoveLibraryItem() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(mockList);

        String input = "Test Book\n1\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        boolean result = controller.handleChoice("2");
        assertFalse(result);
        verify(mockLibrary, times(1)).removeLibraryItem(any(LibraryItem.class));
    }

    @Test
    void testHandleChoiceSearchLibraryItems() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        when(mockLibrary.search(anyString())).thenReturn(mockList);

        String input = "Test\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        boolean result = controller.handleChoice("3");
        assertFalse(result);
        verify(mockLibrary, times(1)).search("Test");
    }

    @Test
    void testHandleChoiceListAllLibraryItems() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        when(mockLibrary.getLibraryItems()).thenReturn(mockList);

        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        boolean result = controller.handleChoice("4");
        assertFalse(result);
    }

    @Test
    void testHandleChoiceSortLibraryItems() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        when(mockLibrary.sortLibraryItems()).thenReturn(mockList);

        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        boolean result = controller.handleChoice("5");
        assertFalse(result);
        verify(mockLibrary, times(1)).sortLibraryItems();
    }

    @Test
    void testHandleChoiceUpdateLibraryItemStatus() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        Book book = TestDataFactory.createSampleBook();
        mockList.add(book);

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(mockList);

        String input = "Test Book\n1\nBORROWED\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        boolean result = controller.handleChoice("6");
        assertFalse(result);
        assertEquals(LibraryItemStatus.BORROWED, book.getStatus());
    }

    @Test
    void testHandleChoiceDisplayStatistics() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        mockList.add(TestDataFactory.createSampleMagazine());
        when(mockLibrary.getLibraryItems()).thenReturn(mockList);

        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        boolean result = controller.handleChoice("7");
        assertFalse(result);

        String output = outputStream.toString();
        assertTrue(output.contains("LIBRARY STATISTICS"));
        assertTrue(output.contains("Total Items"));
    }

    @Test
    void testAddBookWithInvalidDate() {
        String input = "1\nTest Book\nTest Author\ninvalid-date\n2020-01-01\nEXIST\n123-456\nFiction\n100\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, times(1)).addLibraryItem(any(Book.class));
    }

    @Test
    void testAddBookWithInvalidStatus() {
        String input = "1\nTest Book\nTest Author\n2020-01-01\nINVALID\nEXIST\n123-456\nFiction\n100\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, times(1)).addLibraryItem(any(Book.class));
    }

    @Test
    void testAddBookWithInvalidPageCount() {
        String input = "1\nTest Book\nTest Author\n2020-01-01\nEXIST\n123-456\nFiction\n0\n100\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, times(1)).addLibraryItem(any(Book.class));
    }

    @Test
    void testAddMagazine() {
        String input = "2\nTest Magazine\nTest Editor\n2021-01-01\nEXIST\nIssue-1\nTest Publisher\nTest Category\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, times(1)).addLibraryItem(any(Magazine.class));
    }

    @Test
    void testAddReference() {
        String input = "3\nTest Reference\nTest Author\n2022-01-01\nBORROWED\nDictionary\nFirst Edition\nTest Subject\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, times(1)).addLibraryItem(any(Reference.class));
    }

    @Test
    void testAddThesis() {
        String input = "4\nTest Thesis\nTest Author\n2023-01-01\nBANNED\nTest University\nTest Department\nTest Advisor\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, times(1)).addLibraryItem(any(Thesis.class));
    }

    @Test
    void testAddLibraryItemInvalidType() {
        String input = "5\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("1");
        verify(mockLibrary, never()).addLibraryItem(any(LibraryItem.class));

        String output = outputStream.toString();
        assertTrue(output.contains("Invalid type choice"));
    }

    @Test
    void testRemoveLibraryItemEmptyLibrary() {
        GenericLinkedList<LibraryItem> emptyList = new GenericLinkedList<>();
        when(mockLibrary.getLibraryItems()).thenReturn(emptyList);

        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        controller.handleChoice("2");

        String output = outputStream.toString();
        assertTrue(output.contains("No items in library"));
    }

    @Test
    void testRemoveLibraryItemNotFound() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        GenericLinkedList<LibraryItem> emptyList = new GenericLinkedList<>();

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(emptyList);

        String input = "Nonexistent Book\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("2");

        String output = outputStream.toString();
        assertTrue(output.contains("No items found"));
    }

    @Test
    void testRemoveLibraryItemCancel() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(mockList);

        String input = "Test Book\n0\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("2");

        verify(mockLibrary, never()).removeLibraryItem(any(LibraryItem.class));

        String output = outputStream.toString();
        assertTrue(output.contains("Operation cancelled"));
    }

    @Test
    void testRemoveLibraryItemInvalidChoice() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(mockList);

        String input = "Test Book\ninvalid\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("2");

        verify(mockLibrary, never()).removeLibraryItem(any(LibraryItem.class));

        String output = outputStream.toString();
        assertTrue(output.contains("Please enter a valid number"));
    }

    @Test
    void testSearchWithEmptyKeyword() {
        setInput("\n");
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("3");

        String output = outputStream.toString();
        assertTrue(output.contains("Search keyword cannot be empty"));
    }

    @Test
    void testUpdateLibraryItemStatusEmptyLibrary() {
        GenericLinkedList<LibraryItem> emptyList = new GenericLinkedList<>();
        when(mockLibrary.getLibraryItems()).thenReturn(emptyList);

        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        controller.handleChoice("6");

        String output = outputStream.toString();
        assertTrue(output.contains("No items in library"));
    }

    @Test
    void testUpdateLibraryItemStatusNotFound() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        mockList.add(TestDataFactory.createSampleBook());
        GenericLinkedList<LibraryItem> emptyList = new GenericLinkedList<>();

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(emptyList);

        String input = "Nonexistent Book\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("6");

        String output = outputStream.toString();
        assertTrue(output.contains("No items found"));
    }

    @Test
    void testUpdateLibraryItemStatusInvalidStatus() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();
        Book book = TestDataFactory.createSampleBook();
        mockList.add(book);

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);
        when(mockLibrary.search(anyString())).thenReturn(mockList);

        String input = "Test Book\n1\nINVALID\nBORROWED\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("6");

        assertEquals(LibraryItemStatus.BORROWED, book.getStatus());
    }

    @Test
    void testDisplayStatisticsWithVariousStatuses() {
        GenericLinkedList<LibraryItem> mockList = new GenericLinkedList<>();

        Book existBook = TestDataFactory.createSampleBook(1);
        existBook.setStatus(LibraryItemStatus.EXIST);

        Magazine borrowedMagazine = TestDataFactory.createSampleMagazine();
        borrowedMagazine.setStatus(LibraryItemStatus.BORROWED);

        Reference bannedReference = TestDataFactory.createSampleReference();
        bannedReference.setStatus(LibraryItemStatus.BANNED);

        mockList.add(existBook);
        mockList.add(borrowedMagazine);
        mockList.add(bannedReference);

        when(mockLibrary.getLibraryItems()).thenReturn(mockList);

        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        controller.handleChoice("7");

        String output = outputStream.toString();
        assertTrue(output.contains("Total Items: 3"));
        assertTrue(output.contains("Available (EXIST): 1"));
        assertTrue(output.contains("Borrowed: 1"));
        assertTrue(output.contains("Banned: 1"));
    }

    @Test
    void testExitProgram() {
        setInput("\n");
        controller = new CommandLineController(mockLibrary);
        controller.exitProgram();

        verify(mockLibrary, times(1)).writeToFile();

        String output = outputStream.toString();
        assertTrue(output.contains("Saving data to file"));
        assertTrue(output.contains("Thank you"));
    }

    @Test
    void testDisplayLibraryItemsEmpty() {
        GenericLinkedList<LibraryItem> emptyList = new GenericLinkedList<>();
        when(mockLibrary.search(anyString())).thenReturn(emptyList);

        String input = "Nonexistent\n";
        setInput(input);
        controller = new CommandLineController(mockLibrary);

        controller.handleChoice("3");

        String output = outputStream.toString();
        assertTrue(output.contains("No items found"));
    }
}