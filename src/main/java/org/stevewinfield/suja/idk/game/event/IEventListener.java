package org.stevewinfield.suja.idk.game.event;

public interface IEventListener<T extends Event> {
    public void onEvent(T event);
}
