/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.furnitures.IFurnitureInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;

public class DefaultInteractor implements IFurnitureInteractor {

    @Override
    public void onLoaded(final RoomInstance room, final RoomItem item) {
    }

    @Override
    public void onPlace(final RoomPlayer player, final RoomItem item) {
        for (final Vector2 posAct : item.getAffectedTiles()) {
            final GapList<RoomItem> list = item.getRoom().getRoomItemsForTile(posAct);
            int size;
            if ((size = list.size()) > 1) {
                final RoomItem w = list.get(size - 2);
                if (w.getInteractorId() == FurnitureInteractor.ROLLER) {
                    w.requestCycles(IDK.CATA_ROLLERS_ROLL_DELAY);
                    break;
                }
            }
        }
    }

    @Override
    public void onRemove(final RoomPlayer player, final RoomItem item) {
    }

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
    }

    @Override
    public void onCycle(final RoomItem item) {
    }

    @Override
    public void onPlayerWalksOn(final RoomPlayer player, final RoomItem item) {
    }

    @Override
    public void onPlayerWalksOff(final RoomPlayer player, final RoomItem item) {
    }

}
