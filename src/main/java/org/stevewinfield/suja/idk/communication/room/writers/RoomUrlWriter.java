/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomUrlWriter extends MessageWriter {

    public RoomUrlWriter(final String url) {
        super(OperationCodes.getOutgoingOpCode("RoomUrl"));
        super.push(url);
    }

}
