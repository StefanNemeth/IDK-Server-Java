/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class CatalogCanGiftWriter extends MessageWriter {

    public CatalogCanGiftWriter(final int itemId, final boolean giftable) {
        super(OperationCodes.getOutgoingOpCode("CatalogCanGift"));
        super.push(itemId);
        super.push(giftable);
    }

}
