/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.catalog.CatalogItem;

public class CatalogPurchaseResultWriter extends MessageWriter {

    public CatalogPurchaseResultWriter(final int id, final String displayName, final int priceCredits, final int pricePixels, final int priceExtra) {
        super(OperationCodes.getOutgoingOpCode("CatalogPurchaseResult"));
        super.push(id);
        super.push(displayName);
        super.push(priceCredits);
        super.push(pricePixels);
        super.push(priceExtra);
        super.push(0);
        super.push(0);

    }

    public CatalogPurchaseResultWriter(final CatalogItem item, final boolean isGift) {
        super(OperationCodes.getOutgoingOpCode("CatalogPurchaseResult"));
        super.push(item.getBaseItem().getId());
        super.push(item.getDisplayName());
        super.push(item.getCostsCoins());
        super.push(item.getCostsPixels());
        super.push(item.getCostsExtra());
        super.push(!isGift);
        super.push(item.getBaseItem().getType());
        super.push(item.getBaseItem().getSpriteId());
        super.push("");
        super.push(1);
        super.push(0);
        super.push(0);
    }

}
