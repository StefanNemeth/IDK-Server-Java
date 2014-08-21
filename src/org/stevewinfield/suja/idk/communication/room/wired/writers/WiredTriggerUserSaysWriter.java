/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredTriggerUserSaysWriter extends RoomWiredTriggerWriter {

    public WiredTriggerUserSaysWriter(final RoomItem item, final String message, final boolean onlyRoomOwner) {
        super.push(0);
        super.push(0);
        super.push(0);
        super.push(item.getBase().getSpriteId());
        super.push(item.getItemId());
        super.push(message);
        super.push(true);
        super.push(onlyRoomOwner);
    }

}
