/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.game.trading.Trade;

import java.util.Collection;

public class TradeOffersWriter extends MessageWriter {

    public TradeOffersWriter(final Trade trade) {
        super(OperationCodes.getOutgoingOpCode("TradeOffers"));

        final Collection<PlayerItem> itemsOne = trade.getOffersPlayerOne().values();
        final Collection<PlayerItem> itemsTwo = trade.getOffersPlayerTwo().values();

        super.push(trade.getPlayerOne());
        super.push(itemsOne.size());

        for (final PlayerItem item : itemsOne) {
            super.push(item.getItemId());
            super.push(item.getBase().getType().toUpperCase());
            super.push(item.getItemId());
            super.push(item.getBase().getSpriteId());
            super.push(0);
            super.push(item.getBase().isInventoryStackable());
            super.push("");
            super.push(0);
            super.push(0);
            super.push(0);

            if (item.getBase().getType().equals(FurnitureType.FLOOR)) {
                super.push(0);
            }
        }

        super.push(trade.getPlayerTwo());
        super.push(itemsTwo.size());

        for (final PlayerItem item : itemsTwo) {
            super.push(item.getItemId());
            super.push(item.getBase().getType().toUpperCase());
            super.push(item.getItemId());
            super.push(item.getBase().getSpriteId());
            super.push(0);
            super.push(item.getBase().isInventoryStackable());
            super.push("");
            super.push(0);
            super.push(0);
            super.push(0);

            if (item.getBase().getType().equals(FurnitureType.FLOOR)) {
                super.push(0);
            }
        }
    }

}
