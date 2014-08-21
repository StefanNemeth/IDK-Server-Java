/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomPlayerTypingWriter extends MessageWriter {

    public RoomPlayerTypingWriter(final int actorId, final boolean isTyping) {
        super(OperationCodes.getOutgoingOpCode("RoomPlayerTyping"));
        super.push(actorId);
        super.push(isTyping);
    }

}
