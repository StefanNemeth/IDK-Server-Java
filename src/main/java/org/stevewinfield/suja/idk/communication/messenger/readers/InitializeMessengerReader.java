/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerFriendListWriter;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerRequestListWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class InitializeMessengerReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final QueuedMessageWriter queue = new QueuedMessageWriter();
        queue.push(new MessengerFriendListWriter(session.getPlayerMessenger().getBuddies().values()));
        queue.push(new MessengerRequestListWriter(session.getPlayerInstance().getInformation().getId(), session.getPlayerMessenger().getRequests().values()));
        session.writeMessage(queue);
    }

}
