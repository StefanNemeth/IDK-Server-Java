/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomEntryModelWriter extends MessageWriter {

    public RoomEntryModelWriter(final String model, final int roomId) {
        super(OperationCodes.getOutgoingOpCode("RoomEntryModel"));
        super.push(model);
        super.push(roomId);
    }

}
