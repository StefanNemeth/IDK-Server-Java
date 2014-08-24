/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.friendstream.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.friendstream.writers.FriendStreamEventWriter;
import org.stevewinfield.suja.idk.game.friendstream.FriendStreamEventData;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class FriendStreamEventRequestReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.getPlayerInstance().getInformation().isStreamEnabled()) {
            return;
        }

        if (!reader.readBoolean() && !session.getFriendStream().needsUpdate()) {
            return;
        }

        final List<FriendStreamEventData> events = new ArrayList<>(session.getFriendStream().getEvents());
        Collections.reverse(events);

        session.writeMessage(new FriendStreamEventWriter(events));
    }

}
