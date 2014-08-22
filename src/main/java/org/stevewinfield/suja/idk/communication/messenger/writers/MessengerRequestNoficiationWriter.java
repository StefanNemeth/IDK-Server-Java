/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class MessengerRequestNoficiationWriter extends MessageWriter {

    public MessengerRequestNoficiationWriter(final int senderId, final String senderName, final String senderAvatar) {
        super(OperationCodes.getOutgoingOpCode("MessengerRequestNoficiation"));
        super.push(senderId);
        super.push(senderName);
        super.push(senderAvatar);
    }

}
