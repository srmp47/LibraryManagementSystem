package library.observers.listeners;

import library.models.enums.EventType;

public class PrinterListener implements EventListener {

    @Override
    public void update(EventType eventType, String message) {
        System.out.println("Event Type: " + eventType + " | Message: " + message);
    }
}
