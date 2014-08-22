/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomRatingInfoWriter extends MessageWriter {

    public RoomRatingInfoWriter(final int ratingData) {
        super(OperationCodes.getOutgoingOpCode("RoomRatingInfo"));
        super.push(ratingData);
    }

}
