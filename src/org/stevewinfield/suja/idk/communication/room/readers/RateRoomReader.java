/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.RoomRatingInfoWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RateRoomReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(RateRoomReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom())
            return;

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || room.getInformation().getOwnerId() == session.getPlayerInstance().getInformation().getId()
        || room.getVotes().contains(session.getPlayerInstance().getInformation().getId()))
            return;

        room.getVotes().add(session.getPlayerInstance().getInformation().getId());
        try {
            Bootloader
            .getStorage()
            .queryParams(
            "INSERT INTO room_votes (room_id, player_id) VALUES (" + room.getInformation().getId() + ", "
            + session.getPlayerInstance().getInformation().getId() + ")").execute();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }

        room.getInformation().setScore(room.getInformation().getScore() + 1);
        session.writeMessage(new RoomRatingInfoWriter(room.getInformation().getScore()));
    }

}
