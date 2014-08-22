/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomFloorObjectsWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomWallObjectsWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetRoomObjectsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isLoadingRoom() || !session.roomLoadingChecksPassed())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().loadRoomInstance(session.getRoomId());

        if (room == null) {
            return;
        }

        final QueuedMessageWriter queue = new QueuedMessageWriter();
        queue.push(new RoomFloorObjectsWriter(room.getFloorItems()));
        queue.push(new RoomWallObjectsWriter(room.getWallItems()));
        session.writeMessage(queue);

        session.clearLoading();
        room.addPlayerToRoom(session);
    }

}
