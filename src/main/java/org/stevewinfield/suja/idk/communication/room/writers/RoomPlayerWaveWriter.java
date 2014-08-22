/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomPlayerWaveWriter extends MessageWriter {

    public RoomPlayerWaveWriter(final int virtualId) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerWave"));
        super.push(virtualId);
    }

}
