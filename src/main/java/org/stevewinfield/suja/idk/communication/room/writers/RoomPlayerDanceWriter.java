/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomPlayerDanceWriter extends MessageWriter {

    public RoomPlayerDanceWriter(final int playerId, final int danceId) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerDance"));
        super.push(playerId);
        super.push(danceId);
    }

}
