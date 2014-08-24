/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class DefaultSwitchInteractor extends DefaultInteractor {

    @Override
    public void onTrigger(final RoomPlayer player, final RoomItem item, final int request, final boolean hasRights) {
        super.onTrigger(player, item, request, hasRights);

        if (!hasRights && item.getBase().hasRightCheck()) {
            return;
        }

        final int cycles = item.getBase().getCycleCount();
        int state = item.getFlagsState();

        if (state >= cycles) {
            state = 0;
        } else {
            state++;
        }

        item.setFlags(state);
        item.update(player == null ? null : player.getSession());

        if (player != null) {
            item.getRoom().getWiredHandler().onPlayerChangedState(player, item);
        }
    }

}
