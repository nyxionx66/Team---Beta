package dev.mindle.team.event;

import java.lang.reflect.Method;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Map;
import java.util.List;

import dev.mindle.team.Team;

public class EventBus {
    private final Map<Class<? extends Event>, List<EventHandler>> handlers = new ConcurrentHashMap<>();
    private final Map<Object, Integer> listenerCounts = new ConcurrentHashMap<>();
    private final AtomicInteger totalListeners = new AtomicInteger(0);

    public void register(Object listener) {
        Class<?> clazz = listener.getClass();
        int methodsRegistered = 0;
        
        for (Method method : clazz.getDeclaredMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameters = method.getParameterTypes();
                if (parameters.length == 1 && Event.class.isAssignableFrom(parameters[0])) {
                    @SuppressWarnings("unchecked")
                    Class<? extends Event> eventType = (Class<? extends Event>) parameters[0];
                    Subscribe annotation = method.getAnnotation(Subscribe.class);
                    
                    method.setAccessible(true);
                    EventHandler handler = new EventHandler(listener, method, annotation.priority());
                    
                    handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(handler);
                    methodsRegistered++;
                    
                    Team.LOGGER.debug("Registered event handler for {} in {} (priority: {})", 
                        eventType.getSimpleName(), clazz.getSimpleName(), annotation.priority());
                }
            }
        }
        
        if (methodsRegistered > 0) {
            listenerCounts.put(listener, methodsRegistered);
            totalListeners.incrementAndGet();
            Team.LOGGER.debug("Registered listener {} with {} event handlers", 
                clazz.getSimpleName(), methodsRegistered);
        }
    }

    public void unregister(Object listener) {
        Integer handlerCount = listenerCounts.remove(listener);
        if (handlerCount != null) {
            handlers.values().forEach(handlerList -> 
                handlerList.removeIf(handler -> handler.getListener() == listener));
            totalListeners.decrementAndGet();
            Team.LOGGER.debug("Unregistered listener with {} handlers", handlerCount);
        }
    }

    public void post(Event event) {
        List<EventHandler> eventHandlers = handlers.get(event.getClass());
        if (eventHandlers != null && !eventHandlers.isEmpty()) {
            // Sort by priority (higher priority first)
            eventHandlers.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
            
            for (EventHandler handler : eventHandlers) {
                try {
                    handler.invoke(event);
                    if (event.isCancelled()) {
                        Team.LOGGER.debug("Event {} was cancelled by handler", 
                            event.getClass().getSimpleName());
                        break;
                    }
                } catch (Exception e) {
                    Team.LOGGER.error("Error invoking event handler for {}", 
                        event.getClass().getSimpleName(), e);
                }
            }
        }
    }

    public void clear() {
        int totalHandlers = handlers.values().stream().mapToInt(List::size).sum();
        handlers.clear();
        listenerCounts.clear();
        totalListeners.set(0);
        Team.LOGGER.info("Cleared event bus: {} handlers removed", totalHandlers);
    }

    public int getListenerCount() {
        return totalListeners.get();
    }

    public int getHandlerCount() {
        return handlers.values().stream().mapToInt(List::size).sum();
    }

    public int getEventTypeCount() {
        return handlers.size();
    }

    public Map<String, Integer> getEventStatistics() {
        Map<String, Integer> stats = new ConcurrentHashMap<>();
        for (Map.Entry<Class<? extends Event>, List<EventHandler>> entry : handlers.entrySet()) {
            stats.put(entry.getKey().getSimpleName(), entry.getValue().size());
        }
        return stats;
    }

    public boolean hasListeners(Class<? extends Event> eventType) {
        List<EventHandler> eventHandlers = handlers.get(eventType);
        return eventHandlers != null && !eventHandlers.isEmpty();
    }
}