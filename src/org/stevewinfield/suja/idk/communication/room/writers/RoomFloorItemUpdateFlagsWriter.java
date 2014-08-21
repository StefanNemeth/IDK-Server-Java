/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;

public class RoomFloorItemUpdateFlagsWriter extends MessageWriter {

    public RoomFloorItemUpdateFlagsWriter(final RoomItem toUpdate) {
        super(OperationCodes.getOutgoingOpCode("RoomFloorItemUpdateFlags"));
        super.push(toUpdate.getItemId() + "");
        super.push(toUpdate.getFlags());
    }

}
