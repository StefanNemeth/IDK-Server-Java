/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.settings.readers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.settings.writers.RoomUpdatedNotificationWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomInfoWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomWallsStatusWriter;
import org.stevewinfield.suja.idk.game.rooms.*;
import org.stevewinfield.suja.idk.game.rooms.coordination.TileState;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class EditRoomReader implements IMessageReader {
    private static final Logger logger = Logger.getLogger(EditRoomReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null || !room.hasRights(session, true) || reader.readInteger() != room.getInformation().getId()) {
            return;
        }

        final int oldCategory = room.getInformation().getCategoryId();
        final boolean oldDisableBlocking = room.getInformation().blockingDisabled();

        String roomTitle = InputFilter.filterString(reader.readUTF()).trim();
        String roomDescription = InputFilter.filterString(reader.readUTF()).trim();

        int accessType = reader.readInteger();

        if (accessType != RoomAccessType.OPEN && accessType != RoomAccessType.BELL && accessType != RoomAccessType.PASSWORD) {
            accessType = RoomAccessType.OPEN;
        }

        String roomPassword = InputFilter.filterString(reader.readUTF()).trim();

        if (accessType == RoomAccessType.PASSWORD && roomPassword.length() < 1) {
            accessType = RoomAccessType.OPEN;
        }

        int roomUserLimit = reader.readInteger();

        if (roomUserLimit < 10 || roomUserLimit > room.getInformation().getModel().getMaxPlayers()) {
            roomUserLimit = 10;
        }

        int categoryId = reader.readInteger();
        boolean tradingEnabled = false;
        RoomCategory category;

        if ((category = Bootloader.getGame().getRoomManager().getRoomCategory(categoryId)) == null) {
            categoryId = 1;
        } else {
            tradingEnabled = category.isTradingEnabled();
        }

        String tagList = "";
        final int tagCount = reader.readInteger();

        for (int i = 0; i < tagCount; i++) {
            final String tag = reader.readUTF().replace(",", "");

            if (!tag.isEmpty() && tag.length() <= 30) {
                tagList += "," + tag;
            }
        }

        if (tagList.length() > 0) {
            tagList = tagList.substring(1);
        }

        final String[] tags = tagList.split(",");

        final boolean allowPets = reader.readBytes(1)[0] == 65;
        final boolean allowPetsEating = reader.readBytes(1)[0] == 65;
        final boolean disableBlocking = reader.readBytes(1)[0] == 65;
        boolean hideWalls = reader.readBytes(1)[0] == 65;
        int wallThickness = reader.readInteger();
        int floorThickness = reader.readInteger();

        if (hideWalls && !session.getPlayerInstance().hasClub()) {
            hideWalls = false;
        }

        if (wallThickness < -2 || wallThickness > 1) {
            wallThickness = 0;
        }

        if (floorThickness < -2 || floorThickness > 1) {
            floorThickness = 0;
        }

        if (roomTitle.length() > 60) {
            roomTitle = roomTitle.substring(0, 60);
        } else if (roomTitle.length() == 0) {
            roomTitle = "Raum";
        }

        if (roomDescription.length() > 128) {
            roomDescription = roomDescription.substring(0, 128);
        }

        if (roomPassword.length() > 64) {
            roomPassword = roomPassword.substring(0, 64);
        }

        try {
            final PreparedStatement std = Bootloader.getStorage()
                    .queryParams(
                            "UPDATE rooms SET name=?, description=?, " +
                                    "access_type=" + accessType + ", " +
                                    "password=?, max_players=" + roomUserLimit + ", " +
                                    "category_id=" + categoryId + ", " +
                                    "allow_pets=" + (allowPets ? 1 : 0) + ", " +
                                    "allow_pets_eating=" + (allowPetsEating ? 1 : 0) + ", " +
                                    "hide_walls=" + (hideWalls ? 1 : 0) + ", " +
                                    "wall_thickness=" + wallThickness + ", " +
                                    "floor_thickness=" + floorThickness + ", " +
                                    "disable_blocking=" + (disableBlocking ? 1 : 0) + ", " +
                                    "tags=? WHERE id=" + room.getInformation().getId()
                    );
            std.setString(1, roomTitle);
            std.setString(2, roomDescription);
            std.setString(3, roomPassword);
            std.setString(4, tagList);
            std.execute();
            std.close();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
            return;
        }

        final Session owner = room.getInformation().getOwnerId() == session.getPlayerInstance().getInformation().getId() ?
                session :
                Bootloader.getSessionManager().getAuthenticatedSession(room.getInformation().getOwnerId());

        if (owner != null) {
            for (final RoomInformation info : owner.getPlayerInstance().getRooms()) {
                if (info.getId() == room.getInformation().getId()) {
                    info.set(roomTitle, roomDescription, tags, accessType, roomPassword, roomUserLimit,
                            categoryId, allowPets, allowPetsEating, hideWalls,
                            wallThickness, floorThickness, disableBlocking, tradingEnabled);
                    break;
                }
            }
        }

        room.getInformation().set(roomTitle, roomDescription, tags, accessType, roomPassword, roomUserLimit,
                categoryId, allowPets, allowPetsEating, hideWalls,
                wallThickness, floorThickness, disableBlocking, tradingEnabled);

        final MessageWriter wallStatus = new RoomWallsStatusWriter(hideWalls, wallThickness, floorThickness);
        final MessageWriter roomInfo = new RoomInfoWriter(room, false, false);

        session.writeMessage(new RoomUpdatedNotificationWriter(room.getInformation().getId(), 1));
        session.writeMessage(wallStatus);
        session.writeMessage(roomInfo);

        room.writeMessage(new RoomUpdatedNotificationWriter(room.getInformation().getId(), 2), session);
        room.writeMessage(wallStatus, session);
        room.writeMessage(roomInfo, session);

        if (categoryId != oldCategory && Bootloader.getGame().getNavigatorListManager().getNavigatorLists().containsKey(oldCategory)) {
            Bootloader.getGame().getNavigatorListManager().getNavigatorLists().get(oldCategory).removeRoom(room.getInformation());
        }

        if (disableBlocking != oldDisableBlocking) {
            for (final RoomPlayer player : room.getRoomPlayers().values()) {
                if (player.getCurrentTileState() > -1) {
                    final boolean oxi = player.getCurrentTileState() == TileState.SITABLE || player.getCurrentTileState() == TileState.LAYABLE;
                    room.getGamemap().updateTile(
                            player.getPosition().getVector2(),
                            disableBlocking ?
                                    (TileState.PLAYER | (oxi ? player.getCurrentTileState() : 0)) :
                                    (TileState.BLOCKED | (oxi ? player.getCurrentTileState() : 0))
                    );
                }
            }
        }
    }

}
