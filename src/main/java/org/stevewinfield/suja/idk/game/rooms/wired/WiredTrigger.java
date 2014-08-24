/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired;

import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

import java.util.List;

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
