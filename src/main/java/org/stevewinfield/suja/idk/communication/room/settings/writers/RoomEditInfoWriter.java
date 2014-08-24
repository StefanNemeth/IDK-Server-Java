/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.settings.writers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;

public class RoomEditInfoWriter extends MessageWriter {

    public RoomEditInfoWriter(final RoomInstance room) {
        super(OperationCodes.getOutgoingOpCode("RoomEditInfo"));
        super.push(room.getInformation().getId());
        super.push(room.getInformation().getName());
        super.push(room.getInformation().getDescription());
        super.push(room.getInformation().getAccessType());
        super.push(room.getInformation().getCategoryId());
        super.push(room.getInformation().getMaxPlayers());
        super.push(room.getInformation().getModel().getMaxPlayers());
        super.push(room.getInformation().getRoomTags().length);
        for (final String tag : room.getInformation().getRoomTags()) {
            super.push(tag);
        }
        final int size = room.getRights().size();
        super.push(size);
        for (int i = 0; i < size; i++) {
            final int playerId = room.getRights().get(i);
            super.push(playerId);
            super.push(Bootloader.getStorage().readString("SELECT nickname FROM players WHERE id=" + playerId));
        }
        super.push(size);
        super.push(room.getInformation().petsAreAllowed());
        super.push(room.getInformation().petsEatingAllowed());
        super.push(room.getInformation().blockingDisabled()); // room blocking
        // disabled
        super.push(room.getInformation().wallsHidden());
        super.push(room.getInformation().getWallThickness());
        super.push(room.getInformation().getFloorThickness());
    }

}
