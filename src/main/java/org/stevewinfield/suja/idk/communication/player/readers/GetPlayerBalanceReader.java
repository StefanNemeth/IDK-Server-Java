/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.player.writers.ActivityPointsWriter;
import org.stevewinfield.suja.idk.communication.player.writers.CreditsBalanceWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetPlayerBalanceReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }
        final QueuedMessageWriter queue = new QueuedMessageWriter();
        queue.push(new CreditsBalanceWriter(session.getPlayerInstance().getInformation().getCreditsBalance()));
        queue.push(new ActivityPointsWriter(session.getPlayerInstance().getInformation().getPixelsBalance(), session.getPlayerInstance().getInformation().getShellsBalance()));
        session.writeMessage(queue);
    }

}
