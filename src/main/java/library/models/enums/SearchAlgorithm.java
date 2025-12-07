package library.models.enums;

public enum SearchAlgorithm {
    SEARCH_BY_TITLE_EXACT("title contains (case-sensitive)"),
    SEARCH_BY_AUTHOR_EXACT("Exact contains (case-sensitive)"),
    SEARCH_BY_TITLE_IGNORE_CASE("Title contains (case-insensitive)"),
    SEARCH_BY_AUTHOR_IGNORE_CASE("Author contains (case-insensitive)");

    private final String description;

    SearchAlgorithm(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }
}