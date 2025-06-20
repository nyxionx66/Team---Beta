package dev.mindle.team.event;

import java.lang.reflect.Method;

public class EventHandler {
    private final Object listener;
    private final Method method;
    private final int priority;

    public EventHandler(Object listener, Method method, int priority) {
        this.listener = listener;
        this.method = method;
        this.priority = priority;
    }

    public void invoke(Event event) throws Exception {
        method.invoke(listener, event);
    }

    public Object getListener() {
        return listener;
    }

    public Method getMethod() {
        return method;
    }

    public int getPriority() {
        return priority;
    }
}