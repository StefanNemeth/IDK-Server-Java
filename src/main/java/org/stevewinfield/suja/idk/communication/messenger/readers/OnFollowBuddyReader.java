/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerFollowResultWriter;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class OnFollowBuddyReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        final int friendId = reader.readInteger();

        if (!session.getPlayerMessenger().getOnlineBuddies().containsKey(friendId)) {
            return;
        }

        final MessengerBuddy buddy = session.getPlayerMessenger().getOnlineBuddies().get(friendId);

        if (buddy == null || !buddy.isInRoom() || buddy.getSession().getRoomId() == session.getRoomId())
            return;

        session.writeMessage(new MessengerFollowResultWriter(buddy.getSession().getRoomId()));
    }

}
