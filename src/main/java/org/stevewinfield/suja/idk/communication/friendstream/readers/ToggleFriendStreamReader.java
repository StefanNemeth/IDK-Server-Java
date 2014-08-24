/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.friendstream.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ToggleFriendStreamReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        session.getPlayerInstance().getInformation().setStreamEnabled(reader.readBytes(1)[0] == 'A');
        Bootloader.getStorage()
                .executeQuery("UPDATE players SET stream_enabled="
                                + (session.getPlayerInstance().getInformation().isStreamEnabled() ? 1 : 0)
                                + " WHERE id=" + session.getPlayerInstance().getInformation().getId()
                );
    }

}
