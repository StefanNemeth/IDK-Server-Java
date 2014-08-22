/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import java.util.Collection;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubGift;

public class MonthlyClubGiftsWriter extends MessageWriter {

    public MonthlyClubGiftsWriter(final int nextGiftSpan, final int availableGifts, final Collection<CatalogClubGift> gifts) {
        super(OperationCodes.getOutgoingOpCode("MonthlyClubGifts"));
        super.push(nextGiftSpan);
        super.push(availableGifts);
        super.push(gifts.size());

        for (final CatalogClubGift item : gifts)
            super.push(item);

        super.push(gifts.size());

        for (final CatalogClubGift item : gifts) {
            super.push(item.getId());
            super.push(item.onlyVIP());
            super.push(false);
            super.push(1);
        }
    }

}
