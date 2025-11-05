package model;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import model.data_structures.GenericLinkedList;

import java.io.File;
import java.io.IOException;
import java.util.Comparator;
import java.util.List;

public class Library {
    private GenericLinkedList<Book> books;
    private final String dataFile = "library_data.json";
    private final ObjectMapper objectMapper;

    public Library() {
        this.objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());

        this.books = readFromFile();
    }

    public GenericLinkedList<Book> getBooks() {
        return books;
    }

    public void addBook(Book book) {
        this.books.add(book);
    }

    public void removeBook(Book book) {
        books.remove(book);
    }

    public GenericLinkedList<Book> sortBooks() {
        GenericLinkedList<Book> sortedBooks = new GenericLinkedList<>();

        for (Book book : books) {
            sortedBooks.add(book);
        }

        sortedBooks.sort(Comparator.comparing(Book::getPublishDate).reversed());
        return sortedBooks;
    }

    public GenericLinkedList<Book> search(String keyword) {
        return books.filter(book ->
                book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase())
        );
    }


    private GenericLinkedList<Book> readFromFile() {
        try {
            File file = new File(dataFile);
            if (!file.exists()) {
                System.out.println("JSON data file not found. Starting with empty library.");
                return new GenericLinkedList<>();
            }

            List<Book> bookList = objectMapper.readValue(file, new TypeReference<List<Book>>() {});
            GenericLinkedList<Book> loadedBooks = new GenericLinkedList<>();
            for (Book book : bookList) {
                loadedBooks.add(book);
            }

            System.out.println("Books loaded successfully from " + dataFile);
            return loadedBooks;
        } catch (IOException e) {
            System.out.println("Error loading data from JSON file: " + e.getMessage());
            return new GenericLinkedList<>();
        }
    }

    public void writeToFile() {
        try {
            List<Book> bookList = books.toList();
            objectMapper.writerWithDefaultPrettyPrinter().writeValue(new File(dataFile), bookList);
            System.out.println("Books saved successfully to " + dataFile);
        } catch (IOException e) {
            System.out.println("Error saving data to JSON file: " + e.getMessage());
        }
    }
}