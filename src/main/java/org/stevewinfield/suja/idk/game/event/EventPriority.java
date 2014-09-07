package org.stevewinfield.suja.idk.game.event;

public enum EventPriority {
    LOWEST,
    LOW,
    NORMAL,
    HIGH,
    HIGHEST;

    /**
     * Default priority
     */
    public static final EventPriority DEFAULT = EventPriority.NORMAL;
}
