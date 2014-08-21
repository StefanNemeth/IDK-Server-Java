/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredTriggerEnterRoomWriter extends RoomWiredTriggerWriter {
    public WiredTriggerEnterRoomWriter(final RoomItem item, final String specificPlayer) {
        super.push(0);
        super.push(0);
        super.push(0);
        super.push(item.getBase().getSpriteId());
        super.push(item.getItemId());
        super.push(specificPlayer);
        super.push(false);
        super.push(false);
        super.push(7);
        super.push(false);
    }
}
