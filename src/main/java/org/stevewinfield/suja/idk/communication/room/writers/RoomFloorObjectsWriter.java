/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

import java.util.Collection;

public class RoomFloorObjectsWriter extends MessageWriter {

    public RoomFloorObjectsWriter(final Collection<RoomItem> floorItems) {
        super(OperationCodes.getOutgoingOpCode("RoomFloorObjects"));
        super.push(floorItems.size());

        for (final RoomItem item : floorItems) {
            super.push(item);
        }
    }

}
