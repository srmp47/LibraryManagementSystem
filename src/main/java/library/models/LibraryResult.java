package library.models;

import java.time.LocalDateTime;

public class LibraryResult {
    private final boolean success;
    private final String message;
    private final LocalDateTime timestamp;

    public LibraryResult(boolean success, String message) {
        this.success = success;
        this.message = message;
        this.timestamp = LocalDateTime.now();
    }

    public boolean isSuccess() { return success; }
    public String getMessage() { return message; }
    public LocalDateTime getTimestamp() { return timestamp; }

    @Override
    public String toString() {
        String icon = success ? "✅" : "❌";
        return icon + " " + message + " (" + timestamp.toLocalTime() + ")";
    }
}