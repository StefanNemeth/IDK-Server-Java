/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.navigator.writers.NavigatorListRoomsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ListPlayerRoomsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        session.writeMessage(new NavigatorListRoomsWriter(0, 5, "", session.getPlayerInstance().getRooms()));
    }

}
