/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomDoorbellWriter extends MessageWriter {

    public RoomDoorbellWriter(final String nickname) {
        super(OperationCodes.getOutgoingOpCode("RoomDoorbell"));
        super.push(nickname);
    }

    public RoomDoorbellWriter() {
        this("");
    }

}
