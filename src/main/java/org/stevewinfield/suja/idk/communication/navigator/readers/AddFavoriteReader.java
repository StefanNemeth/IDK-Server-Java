/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.NavigatorFavoriteRoomsChangedWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.SQLException;

public class AddFavoriteReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(AddFavoriteReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final int roomId = reader.readInteger();

        if (session.getPlayerInstance().getFavoriteRooms().contains(roomId) || session.getPlayerInstance().getFavoriteRooms().size() >= IDK.NAV_MAX_FAVORITES) {
            return;
        }

        session.getPlayerInstance().getFavoriteRooms().add(roomId);
        session.writeMessage(new NavigatorFavoriteRoomsChangedWriter(roomId, true));

        try {
            Bootloader.getStorage().queryParams("INSERT INTO player_room_favorites (player_id, room_id) VALUES (" + session.getPlayerInstance().getInformation().getId() + ", " + roomId + ")").execute();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

}
