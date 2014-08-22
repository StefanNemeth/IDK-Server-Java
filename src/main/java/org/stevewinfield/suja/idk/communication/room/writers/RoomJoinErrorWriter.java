/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomJoinErrorWriter extends MessageWriter {

    public RoomJoinErrorWriter(final int errorCode) {
        super(OperationCodes.getOutgoingOpCode("RoomJoinError"));
        super.push(errorCode);
    }

}
