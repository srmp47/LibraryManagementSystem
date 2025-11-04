package model;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.stream.Collectors;

public class Library {
    private final ArrayList<Book> books = new ArrayList<Book>();
    public ArrayList<Book> getBooks() {
        return books;
    }
    public void AddBook(Book book){
        this.books.add(book);
    }
    public void removeBook(Book book){
        books.remove(book);
    }
    public ArrayList<Book> sortBooks(){
        ArrayList<Book> sortedBooks = books;
        sortedBooks.sort(Comparator.comparing(Book::getPublishDate).reversed());
        return sortedBooks;
    }
    public ArrayList<Book> search(String keyword){
        return books.stream()
                .filter(book ->
                        book.getTitle().toLowerCase().contains(keyword.toLowerCase()) ||
                        book.getAuthor().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
