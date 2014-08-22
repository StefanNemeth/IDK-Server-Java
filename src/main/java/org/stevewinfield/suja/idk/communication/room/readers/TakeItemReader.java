/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class TakeItemReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true))
            return;

        reader.readInteger();
        final int itemId = reader.readInteger();

        if (!room.getRoomItems().containsKey(itemId))
            return;

        final RoomItem item = room.getRoomItems().get(itemId);

        if (item.getInteractorId() == FurnitureInteractor.POST_IT)
            return;

        room.removeItem(item, session);
        session.getPlayerInstance().getInventory().addItem(item, session, room.itemHasToUpdate(item.getItemId()));
    }

}
