/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomCreateResultReader extends MessageWriter {

    public RoomCreateResultReader(final int roomId, final String roomName) {
        super(OperationCodes.getOutgoingOpCode("RoomCreateResult"));
        super.push(roomId);
        super.push(roomName);
    }

}
