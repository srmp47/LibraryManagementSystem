package library.observers;

import library.models.enums.EventType;
import library.observers.listeners.EventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EventManager {
    Map<EventType, List<EventListener>> listeners = new HashMap<>();

    public EventManager(EventType... operations) {
        for (EventType evenType : operations) {
            this.listeners.put(evenType, new ArrayList<>());
        }
    }

    public void subscribe(EventType eventType, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(eventType);
        eventListeners.add(listener);
    }

    public void unsubscribe(EventType eventType, EventListener listener) {
        List<EventListener> eventListeners = listeners.get(eventType);
        eventListeners.remove(listener);
    }

    public void notify(EventType eventType, String message) {
        List<EventListener> eventListeners = listeners.get(eventType);
        for (EventListener listener : eventListeners) {
            listener.update(eventType, message);
        }
    }
}
