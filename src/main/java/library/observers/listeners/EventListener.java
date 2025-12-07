package library.observers.listeners;

import library.models.enums.EventType;

public interface EventListener {

    public void update(EventType eventType, String message);
}
