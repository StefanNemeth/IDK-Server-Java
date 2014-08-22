/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubGift;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ChooseCatalogClubGiftReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || session.getPlayerInstance().getSubscriptionManager().getAvailableGifts() < 1)
            return;

        final String giftName = reader.readUTF();

        if (!Bootloader.getGame().getCatalogManager().getCatalogClubGifts().containsKey(giftName))
            return;

        final CatalogClubGift gift = Bootloader.getGame().getCatalogManager().getCatalogClubGifts().get(giftName);

        if (!session.getPlayerInstance().hasClub() || (gift.onlyVIP() && !session.getPlayerInstance().hasVIP()))
            return;

        session.getPlayerInstance().getSubscriptionManager().chooseClubGift(gift, session);
    }

}
