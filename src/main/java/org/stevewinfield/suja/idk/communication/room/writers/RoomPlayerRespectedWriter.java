/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomPlayerRespectedWriter extends MessageWriter {

    public RoomPlayerRespectedWriter(final int playerId, final int respects) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerRespected"));
        super.push(playerId);
        super.push(respects);
    }

}
