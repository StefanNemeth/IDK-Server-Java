/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class MessengerInviteReceived extends MessageWriter {

    public MessengerInviteReceived(final int senderId, final String message) {
        super(OperationCodes.getOutgoingOpCode("MessengerInviteReceived"));
        super.push(senderId);
        super.push(message);
    }

}
