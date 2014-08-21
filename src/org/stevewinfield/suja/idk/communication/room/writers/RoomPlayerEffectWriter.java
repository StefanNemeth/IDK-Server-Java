/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomPlayerEffectWriter extends MessageWriter {

    public RoomPlayerEffectWriter(final int virtualId, final int effectId) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerEffect"));
        super.push(virtualId);
        super.push(effectId);
    }

}
