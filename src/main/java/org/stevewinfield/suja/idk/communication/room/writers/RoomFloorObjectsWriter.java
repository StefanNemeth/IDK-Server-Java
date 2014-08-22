/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import java.util.Collection;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class RoomFloorObjectsWriter extends MessageWriter {

    public RoomFloorObjectsWriter(final Collection<RoomItem> floorItems) {
        super(OperationCodes.getOutgoingOpCode("RoomFloorObjects"));
        super.push(floorItems.size());

        for (final RoomItem item : floorItems) {
            super.push(item);
        }
    }

}
