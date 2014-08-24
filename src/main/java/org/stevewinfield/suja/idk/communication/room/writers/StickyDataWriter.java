/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class StickyDataWriter extends MessageWriter {

    public StickyDataWriter(final RoomItem item) {
        super(OperationCodes.getOutgoingOpCode("StickyData"));
        super.push(item.getItemId() + "");
        super.push(item.getTermFlags() != null && item.getTermFlags().length > 1 ? item.getTermFlags()[0] + " " + item.getTermFlags()[1] : "");
    }

}
