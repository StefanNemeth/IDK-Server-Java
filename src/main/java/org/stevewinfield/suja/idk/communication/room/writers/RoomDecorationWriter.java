/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import java.util.Map.Entry;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomDecorationWriter extends MessageWriter {

    public RoomDecorationWriter(final Entry<String, String> entry) {
        super(OperationCodes.getOutgoingOpCode("RoomDecoration"));
        super.push(entry.getKey());
        super.push(entry.getValue());
    }

}
