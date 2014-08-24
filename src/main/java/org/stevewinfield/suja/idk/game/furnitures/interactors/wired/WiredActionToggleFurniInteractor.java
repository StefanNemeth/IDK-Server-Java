/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures.interactors.wired;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.wired.writers.WiredActionToggleFurniWriter;
import org.stevewinfield.suja.idk.game.furnitures.interactors.WiredInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class WiredActionToggleFurniInteractor extends WiredInteractor {

    @Override
    public MessageWriter getWiredDialog(final RoomItem item) {
        return new WiredActionToggleFurniWriter(item, item.getTermFlags().length > 0 ? item.getTermFlags()[0] : "", item.getTermFlags().length > 1 ? Integer.valueOf(item.getTermFlags()[1]) : 0);
    }

}
