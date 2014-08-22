/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomRightsRemovedConfirmationWriter extends MessageWriter {

    public RoomRightsRemovedConfirmationWriter(final int roomId, final int playerId) {
        super(OperationCodes.getOutgoingOpCode("RoomRightsRemovedConfirmation"));
        super.push(roomId);
        super.push(playerId);
    }

}
