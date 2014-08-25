/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class ChatMessage {

    public RoomPlayer getSender() {
        return sender;
    }

    public String getMessage() {
        return message;
    }

    public int getChatType() {
        return type;
    }

    public ChatMessage(final RoomPlayer sender, final String message, final int chatType) {
        this.sender = sender;
        this.message = message;
        this.type = chatType;
    }

    public void dispose() {
        try {
            this.finalize();
        } catch (final Throwable e) {
            Bootloader.getLogger().error("Failed to dipose ChatMessage", e);
        }
    }

    // fields
    private final RoomPlayer sender;
    private final String message;
    private final int type;
}
