/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class MoveFloorItemReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session))
            return;

        final int itemId = reader.readInteger();

        if (!room.getRoomItems().containsKey(itemId))
            return;

        final Vector2 newPosition = new Vector2(reader.readInteger(), reader.readInteger());
        final int rotation = reader.readInteger();

        room.setFloorItem(session, room.getRoomItems().get(itemId), newPosition, rotation);
    }

}
