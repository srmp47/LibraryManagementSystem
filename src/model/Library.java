package model;

import model.data_structures.GenericLinkedList;

import java.util.Comparator;
import java.util.stream.Collectors;

public class Library {
    private final GenericLinkedList<Book> books = new GenericLinkedList<>();

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

    public GenericLinkedList<Book> searchWithStream(String keyword) {
        GenericLinkedList<Book> result = new GenericLinkedList<>();
        books.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                                book.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .forEach(result::add);
        return result;
    }
}