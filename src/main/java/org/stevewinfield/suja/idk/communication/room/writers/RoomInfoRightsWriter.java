/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomInfoRightsWriter extends MessageWriter {

    public RoomInfoRightsWriter(final boolean isFlat, final int roomId, final boolean hasOwnerRights, final String internalName) {
        super(OperationCodes.getOutgoingOpCode("RoomInfoRights"));
        super.push(isFlat);

        if (isFlat) {
            super.push(roomId);
            super.push(hasOwnerRights);
        } else {
            super.push(internalName);
        }
    }

}
