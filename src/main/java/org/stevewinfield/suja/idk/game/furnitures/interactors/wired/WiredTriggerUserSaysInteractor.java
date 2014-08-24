/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors.wired;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.wired.writers.WiredTriggerUserSaysWriter;
import org.stevewinfield.suja.idk.game.furnitures.interactors.WiredInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredTriggerUserSaysInteractor extends WiredInteractor {

    @Override
    public MessageWriter getWiredDialog(final RoomItem item) {
        return new WiredTriggerUserSaysWriter(item, item.getTermFlags().length > 0 ? item.getTermFlags()[0] : "", item.getTermFlags().length > 1 && item.getTermFlags()[1].equals("1"));
    }

}
