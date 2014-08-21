/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class PlayerFavoriteRoomsWriter extends MessageWriter {

    public PlayerFavoriteRoomsWriter(final GapList<Integer> favorites) {
        super(OperationCodes.getOutgoingOpCode("PlayerRoomFavorites"));
        super.push(IDK.NAV_MAX_FAVORITES);
        super.push(favorites.size());

        for (final int id : favorites) {
            super.push(id);
        }
    }

}
