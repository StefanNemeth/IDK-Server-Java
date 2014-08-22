/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetRoomModelReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isLoadingRoom() || !session.roomLoadingChecksPassed())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().loadRoomInstance(session.getRoomId());

        if (room == null) {
            return;
        }

        session.writeMessage(room.getInformation().getModel().getHeightmapWriter());
        session.writeMessage(room.getGamemap().getRelativeMap());
    }

}
