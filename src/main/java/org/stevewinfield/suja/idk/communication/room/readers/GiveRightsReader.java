/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.RoomRightsGivenConfirmationWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GiveRightsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true)) {
            return;
        }

        final int playerId = reader.readInteger();
        final Session playerSession = Bootloader.getSessionManager().getAuthenticatedSession(playerId);

        if (playerSession == null || !playerSession.isInRoom() || playerSession.getRoomId() != room.getInformation().getId()) {
            return;
        }

        if (room.giveRights(playerSession.getRoomPlayer())) {
            session.writeMessage(new RoomRightsGivenConfirmationWriter(room.getInformation().getId(), playerId, playerSession.getPlayerInstance().getInformation().getPlayerName()));
        }
    }

}
