/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredActionShowMessageWriter extends RoomWiredEffectWriter {

    public WiredActionShowMessageWriter(final RoomItem item, final String message) {
        super.push(false);
        super.push(false);
        super.push(false);
        super.push(item.getBase().getSpriteId());
        super.push(item.getItemId());
        super.push(message);
        super.push(0);
        super.push(0);
        super.push(7);
        super.push(0);
        super.push(0);
    }
}
