/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.writers.ClubGiftReadyWriter;
import org.stevewinfield.suja.idk.communication.player.writers.SubscriptionStatusWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetSubscriptionDataReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        session.writeMessage(new SubscriptionStatusWriter(session.getPlayerInstance().getSubscriptionManager(), false));

        final int availableClubGifts = session.getPlayerInstance().getSubscriptionManager().getAvailableGifts();

        if (availableClubGifts > 0)
            session.writeMessage(new ClubGiftReadyWriter(availableClubGifts));

    }
}
