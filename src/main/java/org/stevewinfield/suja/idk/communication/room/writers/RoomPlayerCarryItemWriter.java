/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomPlayerCarryItemWriter extends MessageWriter {

    public RoomPlayerCarryItemWriter(final int uid, final int handItem) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerCarryItem"));
        super.push(uid);
        super.push(handItem);
    }

}
