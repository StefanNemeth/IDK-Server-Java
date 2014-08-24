/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.RoomRightsRemovedConfirmationWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class TakeAllRightsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true)) {
            return;
        }

        for (int i = 0; i < room.getRights().size(); i++) {
            final int playerId = room.getRights().get(i);
            if (playerId > 0 && room.takeRights(playerId)) {
                session.writeMessage(new RoomRightsRemovedConfirmationWriter(room.getInformation().getId(), playerId));
            }
        }
    }
}
