/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.writers.MonthlyClubGiftsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetMonthlyClubGiftsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        session.writeMessage(new MonthlyClubGiftsWriter(session.getPlayerInstance().getSubscriptionManager().getNextGiftSpan(), session.getPlayerInstance().getSubscriptionManager().getAvailableGifts(), Bootloader.getGame().getCatalogManager().getCatalogClubGifts().values()));
    }

}
