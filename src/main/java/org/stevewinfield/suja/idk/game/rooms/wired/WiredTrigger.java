/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired;

import java.util.List;

import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public abstract class WiredTrigger implements IWiredItem {
    public abstract boolean onTrigger(RoomPlayer player, Object data);

    @Override
    public List<Integer> getItems() {
        return null;
    }

    @Override
    public int getDelay() {
        return -1;
    }
}
