/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class VendingInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (player != null && !item.isTouching(player.getPosition(), player.getRotation())) {
            player.moveTo(item.getFrontPosition(), item.getFrontRotation(), item);
            return;
        }

        item.setFlags(1);
        item.update(false, player == null ? null : player.getSession());
        if (player != null) {
            player.handleVending(2, item.getBase().getRandomVendingId());
            item.getRoom().getWiredHandler().onPlayerChangedState(player, item);
        }
        item.requestCycles(2);
    }

    @Override
    public void onCycle(final RoomItem item) {
        super.onCycle(item);
        item.setFlags(0);
        item.update(false);
    }

}
