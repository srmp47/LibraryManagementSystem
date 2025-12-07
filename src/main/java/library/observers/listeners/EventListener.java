package library.observers.listeners;

import library.models.enums.EventType;

public interface EventListener {
    void update(EventType eventType, String message);
}