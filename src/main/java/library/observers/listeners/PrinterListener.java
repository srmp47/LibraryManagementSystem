package library.observers.listeners;

import library.models.enums.EventType;
import library.observers.listeners.EventListener;

public class PrinterListener implements EventListener {
    @Override
    public void update(EventType eventType, String message) {
        System.out.println("📢 Event: " + eventType + " | Message: " + message);
    }
}