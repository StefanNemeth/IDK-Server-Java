/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class RoomItemUpdatedWriter extends MessageWriter {

    public RoomItemUpdatedWriter(final RoomItem item) {
        super(OperationCodes.getOutgoingOpCode("RoomItemUpdated"));
        super.push(item);
    }

}
