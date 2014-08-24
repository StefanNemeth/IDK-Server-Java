/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.messenger.readers;

import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.messenger.writers.MessengerErrorWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class SendInstantMessageReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final int friendId = reader.readInteger();
        final String message = InputFilter.filterString(reader.readUTF());

        if (friendId < 1 || message.length() < 1) {
            return;
        }

        if (!session.getPlayerMessenger().getBuddies().containsKey(friendId)) {
            session.writeMessage(new MessengerErrorWriter(6, friendId));
            return;
        }

        if (!session.getPlayerMessenger().getOnlineBuddies().containsKey(friendId)) {
            session.writeMessage(new MessengerErrorWriter(5, friendId));
            return;
        }

        session.getPlayerMessenger().getOnlineBuddies().get(friendId).getSession().getPlayerMessenger().onMessageReceived(session.getPlayerInstance().getInformation().getId(), message);
    }

}
