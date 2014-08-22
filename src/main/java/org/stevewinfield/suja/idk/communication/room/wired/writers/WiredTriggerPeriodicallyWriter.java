/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredTriggerPeriodicallyWriter extends RoomWiredTriggerWriter {
    public WiredTriggerPeriodicallyWriter(final RoomItem item, final int delay) {
        super.push(false);
        super.push(5);
        super.push(0);
        super.push(item.getBase().getSpriteId());
        super.push(item.getItemId());
        super.push("");
        super.push(1);
        super.push(delay);
        super.push(0);
        super.push(6);
        super.push(0);
    }
}
