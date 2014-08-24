/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.TileState;

public class GateInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (!hasRights && item.getBase().hasRightCheck()) {
            return;
        }

        if (item.updateState(item.getFlagsState() == 0)) {
            item.setFlags(item.getState() == TileState.OPEN ? 1 : 0);
            item.update(player == null ? null : player.getSession());
            if (player != null) {
                item.getRoom().getWiredHandler().onPlayerChangedState(player, item);
            }
        }
    }

}
