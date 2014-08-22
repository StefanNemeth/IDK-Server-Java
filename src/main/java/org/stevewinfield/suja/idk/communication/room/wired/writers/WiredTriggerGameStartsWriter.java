/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredTriggerGameStartsWriter extends RoomWiredTriggerWriter {
    public WiredTriggerGameStartsWriter(final RoomItem item) {
        super.push(false);
        super.push(0);
        super.push(0);
        super.push(item.getBase().getSpriteId());
        super.push(item.getItemId());
        super.push("");
        super.push(0);
        super.push(0);
        super.push(8);
        super.push(0);
        super.push(0);
    }
}