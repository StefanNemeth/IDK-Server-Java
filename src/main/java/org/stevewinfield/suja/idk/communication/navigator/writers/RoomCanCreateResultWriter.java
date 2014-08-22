/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomCanCreateResultWriter extends MessageWriter {

    public RoomCanCreateResultWriter(final boolean error, final int maxRooms) {
        super(OperationCodes.getOutgoingOpCode("RoomCanCreateResult"));
        super.push(error);
        super.push(maxRooms);
    }

}
