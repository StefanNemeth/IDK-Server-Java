/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RoomKickReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session)) {
            return;
        }

        final Session target = Bootloader.getSessionManager().getAuthenticatedSession(reader.readInteger());

        if (target == null || !target.isInRoom() || room.hasRights(target, true)) {
            return;
        }

        final RoomPlayer roomTarget = target.getRoomPlayer();

        if (roomTarget.getRoom().getInformation().getId() != room.getInformation().getId()) {
            return;
        }

        roomTarget.setKicked(true);
        roomTarget.moveTo(room.getInformation().getModel().getDoorPosition().getVector2(), true, false);
    }

}
