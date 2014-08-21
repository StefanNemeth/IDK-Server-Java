/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomWallsStatusWriter extends MessageWriter {

    public RoomWallsStatusWriter(final boolean wallHidden, final int wallThickness, final int floorThickness) {
        super(OperationCodes.getOutgoingOpCode("RoomWallsStatus"));
        super.push(wallHidden);
        super.push(wallThickness);
        super.push(floorThickness);
    }

}
