/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class MessageReceivedWriter extends MessageWriter {

    public MessageReceivedWriter(final int senderId, final String message) {
        super(OperationCodes.getOutgoingOpCode("MessageReceived"));
        super.push(senderId);
        super.push(message);
    }

}
