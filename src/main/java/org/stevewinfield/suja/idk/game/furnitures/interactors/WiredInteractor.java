/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public abstract class WiredInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (!hasRights && item.getBase().hasRightCheck())
            return;

        item.setFlags(1);
        item.update(false);
        item.requestCycles(1);

        final MessageWriter wiredDialog = this.getWiredDialog(item);

        if (wiredDialog != null)
            player.getSession().writeMessage(wiredDialog);
    }

    public abstract MessageWriter getWiredDialog(RoomItem item);

    @Override
    public void onCycle(final RoomItem item) {
        super.onCycle(item);
        item.setFlags("");
        item.update(false);
    }

}
