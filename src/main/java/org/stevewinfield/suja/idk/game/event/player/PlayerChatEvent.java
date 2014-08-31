package org.stevewinfield.suja.idk.game.event.player;

import org.stevewinfield.suja.idk.game.event.Cancellable;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

/**
 * This event is called when a player chats.
 */
public class PlayerChatEvent extends PlayerEvent implements Cancellable {
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public boolean isShouting() {
        return shouting;
    }

    public void setShouting(boolean shouting) {
        this.shouting = shouting;
    }

    public boolean isCancelled() {
        return cancelled;
    }

    public void setCancelled(boolean cancelled) {
        this.cancelled = cancelled;
    }

    public PlayerChatEvent(RoomPlayer player, String message, boolean shouting) {
        super(player);
        this.message = message;
        this.shouting = shouting;
    }

    private String message;
    private boolean shouting;
    private boolean cancelled;
}
