/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.player.writers.AchievementScoreUpdateWriter;
import org.stevewinfield.suja.idk.communication.player.writers.PlayerObjectWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetPlayerInfoReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }
        final QueuedMessageWriter queue = new QueuedMessageWriter();
        queue.push(new PlayerObjectWriter(session.getPlayerInstance().getInformation()));
        queue.push(new AchievementScoreUpdateWriter(session.getPlayerInstance().getInformation().getScore()));
        session.writeMessage(queue);
    }

}
