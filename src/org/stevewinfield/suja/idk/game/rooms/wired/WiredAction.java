/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired;

import java.util.List;

import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public abstract class WiredAction implements IWiredItem {
    public abstract void onHandle(RoomPlayer player);

    public void onHandle(final RoomPlayer player, final WiredDelayEvent event) {
    }

    @Override
    public List<Integer> getItems() {
        return null;
    }

    @Override
    public int getDelay() {
        return -1;
    }
}
