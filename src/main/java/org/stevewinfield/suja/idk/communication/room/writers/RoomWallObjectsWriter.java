/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

import java.util.Collection;

public class RoomWallObjectsWriter extends MessageWriter {

    public RoomWallObjectsWriter(final Collection<RoomItem> wallItems) {
        super(OperationCodes.getOutgoingOpCode("RoomWallObjects"));
        super.push(wallItems.size());

        for (final RoomItem item : wallItems) {
            super.push(item);
        }
    }

}
