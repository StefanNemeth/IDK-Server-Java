/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

import java.util.Map.Entry;

public class RoomDecorationWriter extends MessageWriter {

    public RoomDecorationWriter(final Entry<String, String> entry) {
        super(OperationCodes.getOutgoingOpCode("RoomDecoration"));
        super.push(entry.getKey());
        super.push(entry.getValue());
    }

}
