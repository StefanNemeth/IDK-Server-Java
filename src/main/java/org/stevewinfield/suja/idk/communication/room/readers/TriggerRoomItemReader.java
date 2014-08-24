/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class TriggerRoomItemReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());
        RoomItem item = null;

        if (room == null || (item = room.getRoomItems().get(reader.readInteger())) == null) {
            return;
        }

        item.getInteractor().onTrigger(session.getRoomPlayer(), item, reader.getMessageId() == OperationCodes.getIncomingOpCode("ItemClearDice") ? 1 : reader.readInteger(), room.hasRights(session));
    }

}
