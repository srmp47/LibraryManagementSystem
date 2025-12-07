package library.strategies;

import library.models.LibraryItem;

public interface SearchStrategy {
    boolean matches(LibraryItem item, String searchTerm);
}