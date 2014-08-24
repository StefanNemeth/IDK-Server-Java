/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.RoomPlayerRespectedWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RespectPlayerReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || session.getPlayerInstance().getInformation().getAvailableRespects() < 1) {
            return;
        }

        final Session targetSession = Bootloader.getSessionManager().getAuthenticatedSession(reader.readInteger());

        if (targetSession == null || targetSession.getRoomId() != session.getRoomId()) {
            return;
        }

        room.writeMessage(new RoomPlayerRespectedWriter(
                        targetSession.getPlayerInstance().getInformation().getId(),
                        targetSession.getPlayerInstance().getInformation().getRespectPoints() + 1
                ),
                session
        );

        session.getPlayerInstance().getInformation().decrementAvailableRespects();
        targetSession.getPlayerInstance().getInformation().incrementRespectPoints();

    }

}
