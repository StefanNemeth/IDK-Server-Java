/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.writers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;

import java.util.Collection;

public class NavigatorListRoomsWriter extends MessageWriter {

    public static void serializeRoom(final MessageWriter writer, final RoomInformation room) {
        RoomInstance instance = null;
        if (room.getTotalPlayers() == 0) {
            instance = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(room.getId());
        }
        writer.push(room.getId());
        writer.push(false); // showing event todo
        writer.push(room.getName()); // also eventname todo
        writer.push(room.getOwnerName()); // owner name.. todo
        writer.push(room.getAccessType());
        writer.push((room.getTotalPlayers() == 0 && instance != null ? instance.getInformation().getTotalPlayers() : room.getTotalPlayers()));
        writer.push(room.getMaxPlayers());
        writer.push(room.getDescription()); // also eventdescription todo
        writer.push(0);
        writer.push(room.isTradingEnabled());
        writer.push(room.getScore());
        writer.push(room.getCategoryId()); // also eventid todo
        writer.push(""); // event started? todo
        writer.push(room.getRoomTags().length);
        for (final String tag : room.getRoomTags()) {
            writer.push(tag);
        }
        writer.push(0); // background image id
        writer.push(0); // overlay image id
        writer.push(0); // objects amount
        writer.push(0);
        writer.push(1);
    }

    public NavigatorListRoomsWriter(final int categoryId, final int mode, final String query, final Collection<RoomInformation> rooms) {
        super(OperationCodes.getOutgoingOpCode("NavigatorListRooms"));
        super.push(categoryId);
        super.push(mode);
        super.push(query);
        super.push(rooms.size());

        for (final RoomInformation room : rooms) {
            NavigatorListRoomsWriter.serializeRoom(this, room);
        }
    }

}
