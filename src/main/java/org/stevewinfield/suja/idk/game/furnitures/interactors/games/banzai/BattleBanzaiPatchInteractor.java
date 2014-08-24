/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors.games.banzai;

import org.stevewinfield.suja.idk.game.furnitures.IFurnitureInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class BattleBanzaiPatchInteractor implements IFurnitureInteractor {

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
        room.getRoomTask().getBanzaiTask().addGameItem(item);
    }

    @Override
    public void onPlace(final RoomPlayer player, final RoomItem item) {
    }

    @Override
    public void onRemove(final RoomPlayer player, final RoomItem item) {
        item.getRoom().getRoomTask().getBanzaiTask().removeGameItem(item);
    }

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
    }

    @Override
    public void onCycle(final RoomItem item) {
        if (item.getRoom().getRoomTask().getBanzaiTask().isRunning()) {
            item.setCounter(0);
            return;
        }
        item.setFlags(item.getFlagsState() == 0 ? item.getRoom().getRoomTask().getBanzaiTask().getFlexInteger() + "" : "0");
        item.update(false, true);
        if (item.getIncementedCounter() < 5) {
            item.requestCycles(1);
        }
    }

    @Override
    public void onPlayerWalksOn(final RoomPlayer player, final RoomItem item) {
        if (!item.getRoom().getRoomTask().getBanzaiTask().isRunning()) {
            return;
        }

        item.getRoom().getRoomTask().getBanzaiTask().onHandle(player, item);
    }

    @Override
    public void onPlayerWalksOff(final RoomPlayer player, final RoomItem item) {
    }

}
