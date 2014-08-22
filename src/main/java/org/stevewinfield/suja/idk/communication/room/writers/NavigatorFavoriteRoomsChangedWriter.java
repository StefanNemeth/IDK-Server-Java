/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class NavigatorFavoriteRoomsChangedWriter extends MessageWriter {

    public NavigatorFavoriteRoomsChangedWriter(final int roomId, final boolean added) {
        super(OperationCodes.getOutgoingOpCode("NavigatorFavoriteRoomsChanged"));
        super.push(roomId);
        super.push(added);
    }

}
