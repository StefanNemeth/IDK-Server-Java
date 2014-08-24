/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;

import java.util.Collection;

public class MessengerFriendListWriter extends MessageWriter {

    public MessengerFriendListWriter(final Collection<MessengerBuddy> buddies) {
        super(OperationCodes.getOutgoingOpCode("MessengerFriendList"));
        super.push(600);
        super.push(200);
        super.push(600);
        super.push(900);
        super.push(false);
        super.push(buddies.size());

        for (final MessengerBuddy buddy : buddies) {
            super.push(buddy);
        }
    }

}
