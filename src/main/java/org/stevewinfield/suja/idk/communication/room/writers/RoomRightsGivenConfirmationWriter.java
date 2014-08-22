/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomRightsGivenConfirmationWriter extends MessageWriter {

    public RoomRightsGivenConfirmationWriter(final int roomId, final int playerId, final String name) {
        super(OperationCodes.getOutgoingOpCode("RoomRightsGivenConfirmation"));
        super.push(roomId);
        super.push(playerId);
        super.push(name);
    }

}
