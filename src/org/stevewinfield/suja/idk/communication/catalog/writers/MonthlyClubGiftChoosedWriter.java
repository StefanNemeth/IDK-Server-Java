/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.catalog.CatalogClubGift;

public class MonthlyClubGiftChoosedWriter extends MessageWriter {

    public MonthlyClubGiftChoosedWriter(final String product, final CatalogClubGift gift) {
        super(OperationCodes.getOutgoingOpCode("MonthlyClubGiftChoosed"));
        super.push(product);
        super.push(1);
        super.push(gift);
    }

}
