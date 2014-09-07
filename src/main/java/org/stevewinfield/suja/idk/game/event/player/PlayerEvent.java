package org.stevewinfield.suja.idk.game.event.player;

import org.stevewinfield.suja.idk.game.event.Event;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public abstract class PlayerEvent extends Event {
    public RoomPlayer getPlayer() {
        return player;
    }

    public PlayerEvent(RoomPlayer player) {
        this.player = player;
    }

    private final RoomPlayer player;
}
