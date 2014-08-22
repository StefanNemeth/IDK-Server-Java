/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired.triggers;

import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredTrigger;

public class WiredTriggerGameEnds extends WiredTrigger {

    public WiredTriggerGameEnds(final RoomInstance room, final RoomItem item, final String[] data) {
        this.room = room;
        this.item = item;
        this.set(data);
    }

    @Override
    public int getWiredType() {
        return 1;
    }

    @Override
    public RoomItem getItem() {
        return item;
    }

    @Override
    public void set(final String[] obj) {
    }

    @Override
    public String[] getObject(final MessageReader reader) {
        return new String[] {};
    }

    @Override
    public boolean onTrigger(final RoomPlayer player, final Object data) {
        return true;
    }

    // fields
    private final RoomInstance room;
    private final RoomItem item;
}
