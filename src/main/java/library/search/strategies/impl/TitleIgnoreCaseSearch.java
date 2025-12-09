package library.search.strategies.impl;

import library.models.LibraryItem;
import library.search.strategies.SearchStrategy;

public class TitleIgnoreCaseSearch implements SearchStrategy {
    @Override
    public boolean matches(LibraryItem item, String searchTerm) {
        return item.getTitle().toLowerCase().contains(searchTerm.toLowerCase());
    }
}