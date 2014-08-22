/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class UpdatePlayerInventoryWriter extends MessageWriter {

    public UpdatePlayerInventoryWriter() {
        super(OperationCodes.getOutgoingOpCode("UpdatePlayerInventory"));
    }

}
