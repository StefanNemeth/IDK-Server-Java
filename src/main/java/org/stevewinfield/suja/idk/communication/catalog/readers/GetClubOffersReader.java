/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.readers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogClubOffersWriter;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubOffer;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubOfferType;
import org.stevewinfield.suja.idk.game.levels.ClubSubscriptionLevel;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetClubOffersReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        final GapList<CatalogClubOffer> correctedOffers = new GapList<CatalogClubOffer>();

        for (final CatalogClubOffer offer : Bootloader.getGame().getCatalogManager().getCatalogClubOffers().values()) {
            if (session.getPlayerInstance().getSubscriptionManager().getBaseLevel() > ClubSubscriptionLevel.BASIC
            && offer.getType() == CatalogClubOfferType.BASIC)
                continue;
            correctedOffers.add(offer);
        }

        session.writeMessage(new CatalogClubOffersWriter(correctedOffers, Bootloader.getTimestamp()));
    }

}
