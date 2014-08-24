/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.messenger.MessengerRequest;

import java.util.Collection;

public class MessengerRequestListWriter extends MessageWriter {

    public MessengerRequestListWriter(final int playerId, final Collection<MessengerRequest> requests) {
        super(OperationCodes.getOutgoingOpCode("MessengerRequestList"));
        super.push(requests.size());
        super.push(requests.size());

        for (final MessengerRequest request : requests) {
            super.push(request);
        }
    }

}
