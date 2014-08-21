/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;

public class MessengerUpdateListWriter extends MessageWriter {

    public MessengerUpdateListWriter(final MessengerBuddy buddy, final int mode) {
        super(OperationCodes.getOutgoingOpCode("MessengerUpdateList"));
        super.push(0);
        super.push(1);
        super.push(mode);
        super.push(buddy);
    }

    public MessengerUpdateListWriter(final MessengerBuddy buddy) {
        this(buddy, 0);
    }

}
