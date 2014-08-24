/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;

import java.util.List;

public class InventoryNewItemsWriter extends MessageWriter {

    public InventoryNewItemsWriter(final List<PlayerItem> floorItems, final List<PlayerItem> wallItems) {
        super(OperationCodes.getOutgoingOpCode("InventoryNewItems"));
        super.push(floorItems.size() > 0 && wallItems.size() > 0 ? 2 : 1);
        if (floorItems.size() > 0) {
            super.push(1);
            super.push(floorItems.size());

            for (final PlayerItem item : floorItems) {
                super.push(item.getItemId());
            }
        }
        if (wallItems.size() > 0) {
            super.push(2);
            super.push(wallItems.size());

            for (final PlayerItem item : wallItems) {
                super.push(item.getItemId());
            }
        }
    }

}
