/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;

import java.util.Collection;

public class InventoryObjectsWriter extends MessageWriter {

    public InventoryObjectsWriter(final char type, final Collection<PlayerItem> items) {
        super(OperationCodes.getOutgoingOpCode("InventoryObjects"));
        super.push(type + "");
        super.push(true);
        super.push(true);
        super.push(items.size());

        for (final PlayerItem item : items) {
            super.push(item);
        }
    }

}
