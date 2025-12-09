package library.search.strategies;

import library.models.enums.SearchAlgorithm;
import library.search.strategies.impl.AuthorExactSearch;
import library.search.strategies.impl.AuthorIgnoreCaseSearch;
import library.search.strategies.impl.TitleExactSearch;
import library.search.strategies.impl.TitleIgnoreCaseSearch;

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