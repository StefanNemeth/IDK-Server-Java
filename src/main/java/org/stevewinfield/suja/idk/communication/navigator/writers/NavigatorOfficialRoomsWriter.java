/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.writers;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.navigator.OfficialItem;
import org.stevewinfield.suja.idk.game.navigator.OfficialItemImageType;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;

import java.sql.ResultSet;
import java.sql.SQLException;

public class NavigatorOfficialRoomsWriter extends MessageWriter {
    private static Logger logger = Logger.getLogger(NavigatorOfficialRoomsWriter.class);

    public static void serializeItem(final MessageWriter writer, final OfficialItem item) {
        RoomInformation instance = null;

        if (!item.isCategory()) {
            if (Bootloader.getGame().getRoomManager().getLoadedRoomInstance(item.getRoomId()) != null) {
                instance = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(item.getRoomId()).getInformation();
            } else {
                try {
                    final ResultSet roomRow = Bootloader.getStorage().queryParams("SELECT rooms.*, nickname FROM rooms, players WHERE rooms.id=" + item.getRoomId()).executeQuery();
                    if (roomRow.next()) {
                        final RoomInformation info = new RoomInformation();
                        info.set(roomRow);
                        instance = info;
                    }
                    roomRow.close();
                } catch (final SQLException e) {
                    logger.error("SQL Exception", e);
                }
            }
        }

        int type = 3;

        if (item.isCategory()) {
            type = 4;
        } else {
            type = 2;
        }

        writer.push(item.getId());
        writer.push(item.getName());
        writer.push(item.getDescription());
        writer.push(item.getDisplayType());
        writer.push(item.getBannerLabel());
        writer.push(item.getImageType() == OfficialItemImageType.EXTERNAL ? item.getImage() : "");
        writer.push(item.getParentId());
        writer.push(instance != null ? instance.getTotalPlayers() : 0);
        writer.push(type);

        if (item.isCategory()) {
            writer.push(item.autoExpand());
        }

        if (instance != null) {
            NavigatorListRoomsWriter.serializeRoom(writer, instance);
        }
    }

    public NavigatorOfficialRoomsWriter(final GapList<OfficialItem> items) {
        super(OperationCodes.getOutgoingOpCode("NavigatorOfficialRooms"));
        super.push(items.size());

        for (final OfficialItem item : items) {
            if (item.getParentId() > 0) {
                continue;
            }

            NavigatorOfficialRoomsWriter.serializeItem(this, item);

            if (item.isCategory()) {
                for (final OfficialItem child : items) {
                    if (child.getParentId() != item.getId()) {
                        continue;
                    }

                    NavigatorOfficialRoomsWriter.serializeItem(this, child);
                }
            }
        }

    }

}
