/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.friendstream.writers;

import java.util.Collection;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.friendstream.FriendStreamEventData;

public class FriendStreamEventWriter extends MessageWriter {

    public FriendStreamEventWriter(final Collection<FriendStreamEventData> events) {
        super(OperationCodes.getOutgoingOpCode("FriendStreamEvent"));
        super.push(events.size());

        for (final FriendStreamEventData item : events)
            super.push(item);
    }

}
