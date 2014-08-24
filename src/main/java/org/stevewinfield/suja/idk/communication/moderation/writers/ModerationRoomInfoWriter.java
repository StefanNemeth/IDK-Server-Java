/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.moderation.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class ModerationRoomInfoWriter extends MessageWriter {

    public ModerationRoomInfoWriter(final RoomInformation roomInfo, final RoomInstance instance) {
        super(OperationCodes.getOutgoingOpCode("ModerationRoomInfo"));
        super.push(roomInfo.getId());
        super.push(roomInfo.getTotalPlayers());

        boolean foundOwner = false;

        if (instance != null) {
            for (final RoomPlayer player : instance.getRoomPlayers().values()) {
                if (!player.isBot() && player.getSession() != null && player.getPlayerInformation().getId() == roomInfo.getOwnerId()) {
                    foundOwner = true;
                    break;
                }
            }
        }

        super.push(foundOwner);
        super.push(roomInfo.getOwnerId());
        super.push(roomInfo.getOwnerName());
        super.push(roomInfo.getId());
        super.push(roomInfo.getName());
        super.push(roomInfo.getDescription());
        super.push(0); // tag count TODO
        super.push(false); // event? TODO
    }

}
