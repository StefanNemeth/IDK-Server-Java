/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubOffer;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubOfferType;

import java.util.Calendar;
import java.util.Date;

public class CatalogClubOffersWriter extends MessageWriter {

    public CatalogClubOffersWriter(final GapList<CatalogClubOffer> offers, final long baseTimestamp) {
        super(OperationCodes.getOutgoingOpCode("CatalogClubOffers"));
        super.push(offers.size());

        for (final CatalogClubOffer offer : offers) {
            final Date date = Bootloader.getDateFromTimestamp(baseTimestamp + offer.getLengthSeconds());
            final Calendar expire = Calendar.getInstance();
            expire.setTime(date);

            super.push(offer.getId());
            super.push(offer.getName());
            super.push(offer.getPrice());
            super.push(false); // upgrade? nop
            super.push(offer.getType() == CatalogClubOfferType.VIP);
            super.push(offer.getLengthMonths());
            super.push(offer.getLengthDays());
            super.push(expire.get(Calendar.YEAR));
            super.push(expire.get(Calendar.MONTH));
            super.push(expire.get(Calendar.DAY_OF_MONTH));
        }
    }

}
