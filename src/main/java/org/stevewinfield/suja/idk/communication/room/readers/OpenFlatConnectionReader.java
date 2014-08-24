/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomType;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class OpenFlatConnectionReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final int roomId = reader.readInteger();
        final String password = reader.readUTF();

        final RoomInstance instance = Bootloader.getGame().getRoomManager().loadRoomInstance(roomId);

        if (instance == null || instance.getInformation().getRoomType() == RoomType.PUBLIC) {
            return;
        }

        session.prepareRoom(instance, password);
    }

}