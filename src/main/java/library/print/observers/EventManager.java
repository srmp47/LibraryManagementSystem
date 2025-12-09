package library.print.observers;

import library.models.enums.EventType;
import library.print.observers.listeners.EventListener;
import library.print.observers.listeners.PrinterListener;
import java.util.*;

public class EventManager {
    private static EventManager eventBus;
    private final Map<EventType, List<EventListener>> listeners;
    private final PrinterListener listener;

    private EventManager() {
        listeners = new EnumMap<>(EventType.class);
        Arrays.stream(EventType.values())
                .forEach(type -> listeners.put(type, new ArrayList<>()));

        listener = new PrinterListener();
        subscribeToAll(listener);
    }

    public static synchronized EventManager getEventBus() {
        if (eventBus == null) {
            eventBus = new EventManager();
        }
        return eventBus;
    }

    private void subscribeToAll(EventListener listener) {
        Arrays.stream(EventType.values())
                .forEach(type -> subscribe(type, listener));
    }

    public void subscribe(EventType eventType, EventListener listener) {
        var eventListeners = listeners.get(eventType);
        synchronized (eventListeners) {
            if (!eventListeners.contains(listener)) {
                eventListeners.add(listener);
            }
        }
    }

    public void unsubscribe(EventType eventType, EventListener listener) {
        var eventListeners = listeners.get(eventType);
        synchronized (eventListeners) {
            eventListeners.remove(listener);
        }
    }

    public void publish(EventType eventType, String message) {
        var eventListeners = listeners.get(eventType);
        List<EventListener> listenersCopy;
        synchronized (eventListeners) {
            listenersCopy = new ArrayList<>(eventListeners);
        }

        listenersCopy.forEach(listener -> listener.update(eventType, message));
    }
}