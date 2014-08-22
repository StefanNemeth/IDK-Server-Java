/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class PlayerHomeRoomWriter extends MessageWriter {

    public PlayerHomeRoomWriter(final int homeRoom) {
        super(OperationCodes.getOutgoingOpCode("PlayerHomeRoom"));
        super.push(homeRoom);
    }

}
