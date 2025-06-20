package dev.mindle.team.event.events;

import dev.mindle.team.event.Event;

public class ChatEvent extends Event {
    private final String message;
    private final boolean fromPlayer;

    public ChatEvent(String message, boolean fromPlayer) {
        this.message = message;
        this.fromPlayer = fromPlayer;
    }

    public String getMessage() { return message; }
    public boolean isFromPlayer() { return fromPlayer; }
}