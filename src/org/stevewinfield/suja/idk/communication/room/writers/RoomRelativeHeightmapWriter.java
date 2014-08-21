/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomRelativeHeightmapWriter extends MessageWriter {

    public RoomRelativeHeightmapWriter(final String heightmap) {
        super(OperationCodes.getOutgoingOpCode("RoomRelativeHeightmap"));
        super.push(heightmap);
    }

}
