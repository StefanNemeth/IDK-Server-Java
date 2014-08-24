/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.navigator.writers.RoomCreateResultReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class CreateRoomReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(CreateRoomReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || (session.getPlayerInstance().getRooms().size() >= IDK.NAV_MAX_ROOMS_PER_PLAYER && !session.getPlayerInstance().hasRight("unlimited_rooms"))) {
            return;
        }

        final String roomName = InputFilter.filterString(reader.readUTF());
        final String modelName = reader.readUTF().toLowerCase();

        if (roomName.length() < 3 || roomName.length() >= 26 || modelName.length() >= 10 || Bootloader.getGame().getRoomManager().getRoomModel(modelName) == null) {
            return;
        }

        int id = 0;
        try {
            final PreparedStatement std = Bootloader.getStorage().queryParams("INSERT INTO rooms (owner_id, name, description, model_name) VALUES (" + session.getPlayerInstance().getInformation().getId() + ", ?, '', ?)");
            std.setString(1, roomName);
            std.setString(2, modelName);
            std.execute();
            std.close();

            id = Bootloader.getStorage().readLastId("rooms");
            final ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM rooms WHERE id=" + id).executeQuery();

            if (!row.next() || id < 1) {
                return;
            }

            final RoomInformation info = new RoomInformation();
            info.set(row, session.getPlayerInstance().getInformation().getPlayerName());

            session.getPlayerInstance().addRoomToList(info);
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
            return;
        }
        session.writeMessage(new RoomCreateResultReader(id, roomName));
    }

}
