/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors;

import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class WiredPlateInteractor extends DefaultInteractor {

    @Override
    public void onPlayerWalksOn(final RoomPlayer player, final RoomItem item) {
        super.onPlayerWalksOn(player, item);
        item.setFlags(1);
        item.update(false, true);
    }

    @Override
    public void onPlayerWalksOff(final RoomPlayer player, final RoomItem item) {
        super.onPlayerWalksOff(player, item);
        item.setFlags(0);
        item.update(false, true);
    }

}
