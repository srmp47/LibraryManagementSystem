package library.strategies.impl;

import library.models.LibraryItem;
import library.strategies.SearchStrategy;

public class TitleExactSearch implements SearchStrategy {
    @Override
    public boolean matches(LibraryItem item, String searchTerm) {
        return item.getTitle().contains(searchTerm);
    }
}