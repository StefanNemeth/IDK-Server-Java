/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomWallItemRemovedWriter extends MessageWriter {

    public RoomWallItemRemovedWriter(final int itemId) {
        super(OperationCodes.getOutgoingOpCode("RoomWallItemRemoved"));
        super.push(itemId + "");
        super.push("");
        super.push(false);
    }

}
