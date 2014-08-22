/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.NavigatorFavoriteRoomsChangedWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RemoveFavoriteReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(RemoveFavoriteReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        final int roomId = reader.readInteger();

        if (!session.getPlayerInstance().getFavoriteRooms().contains(roomId))
            return;

        session.getPlayerInstance().getFavoriteRooms().remove(new Integer(roomId));
        session.writeMessage(new NavigatorFavoriteRoomsChangedWriter(roomId, false));

        try {
            Bootloader
            .getStorage()
            .queryParams(
            "DELETE FROM player_room_favorites WHERE player_id=" + session.getPlayerInstance().getInformation().getId()
            + " AND room_id=" + roomId).execute();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

}
