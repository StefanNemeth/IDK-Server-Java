/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.settings.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomUpdatedNotificationWriter extends MessageWriter {

    public RoomUpdatedNotificationWriter(final int roomId, final int type) {
        super(OperationCodes.getOutgoingOpCode("RoomUpdatedNotification" + type));
        super.push(roomId);
    }

}
