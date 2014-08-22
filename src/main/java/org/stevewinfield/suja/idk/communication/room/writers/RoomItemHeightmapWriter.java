/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomItemHeightmapWriter extends MessageWriter {

    public RoomItemHeightmapWriter(final String heightmap) {
        super(OperationCodes.getOutgoingOpCode("RoomItemHeightmap"));
        super.push(heightmap);
    }

}
