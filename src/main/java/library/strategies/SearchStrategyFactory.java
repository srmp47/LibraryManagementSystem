package library.strategies;

import library.models.enums.SearchAlgorithm;
import library.strategies.impl.AuthorExactSearch;
import library.strategies.impl.AuthorIgnoreCaseSearch;
import library.strategies.impl.TitleExactSearch;
import library.strategies.impl.TitleIgnoreCaseSearch;

public class SearchStrategyFactory {

    public static SearchStrategy createStrategy(SearchAlgorithm algorithm) {
        return switch (algorithm) {
            case SEARCH_BY_TITLE_EXACT -> new TitleExactSearch();
            case SEARCH_BY_AUTHOR_EXACT -> new AuthorExactSearch();
            case SEARCH_BY_TITLE_IGNORE_CASE -> new TitleIgnoreCaseSearch();
            case SEARCH_BY_AUTHOR_IGNORE_CASE -> new AuthorIgnoreCaseSearch();
        };
    }
}