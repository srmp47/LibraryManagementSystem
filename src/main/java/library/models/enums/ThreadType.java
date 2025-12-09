package library.models.enums;

public enum ThreadType {
    REQUEST_HANDLER("Request Handler Thread", "Handles user input and menu operations"),
    RESULT_HANDLER("Result Handler Thread", "Processes library requests and generates results");

    private final String displayName;
    private final String description;

    ThreadType(String displayName, String description) {
        this.displayName = displayName;
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getDescription() {
        return description;
    }

    @Override
    public String toString() {
        return displayName + " - " + description;
    }
}