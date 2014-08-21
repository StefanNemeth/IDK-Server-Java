/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class MessengerErrorWriter extends MessageWriter {

    public MessengerErrorWriter(final int errorId, final int conversation) {
        super(OperationCodes.getOutgoingOpCode("MessengerError"));
        super.push(errorId);
        super.push(conversation);
    }

}
