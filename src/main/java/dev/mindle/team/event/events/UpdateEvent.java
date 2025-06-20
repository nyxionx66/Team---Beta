package dev.mindle.team.event.events;

import dev.mindle.team.event.Event;

public class UpdateEvent extends Event {
    public static class Pre extends UpdateEvent {}
    public static class Post extends UpdateEvent {}
}