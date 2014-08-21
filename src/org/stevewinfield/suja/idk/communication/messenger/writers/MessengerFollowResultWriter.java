/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class MessengerFollowResultWriter extends MessageWriter {

    public MessengerFollowResultWriter(final int roomId) {
        super(OperationCodes.getOutgoingOpCode("MessengerFollowResult"));
        super.push(false);
        super.push(roomId);
    }

}
