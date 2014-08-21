/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class InventoryItemRemovedWriter extends MessageWriter {

    public InventoryItemRemovedWriter(final int itemId) {
        super(OperationCodes.getOutgoingOpCode("InventoryItemRemoved"));
        super.push(itemId);
    }

}
