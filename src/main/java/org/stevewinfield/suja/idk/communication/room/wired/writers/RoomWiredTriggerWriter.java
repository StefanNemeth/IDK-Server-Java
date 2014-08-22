/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.wired.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomWiredTriggerWriter extends MessageWriter {

    public RoomWiredTriggerWriter() {
        super(OperationCodes.getOutgoingOpCode("RoomWiredTrigger"));
    }

}
