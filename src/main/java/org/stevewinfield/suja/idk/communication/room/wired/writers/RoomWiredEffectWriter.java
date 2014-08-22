/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomWiredEffectWriter extends MessageWriter {

    public RoomWiredEffectWriter() {
        super(OperationCodes.getOutgoingOpCode("RoomWiredEffect"));
    }

}
