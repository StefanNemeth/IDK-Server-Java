/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.recycler.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class CatalogRecyclerResultWriter extends MessageWriter {

    public CatalogRecyclerResultWriter(final boolean success, final int itemId) {
        super(OperationCodes.getOutgoingOpCode("CatalogRecyclerResult"));
        super.push(success);
        super.push(itemId);
    }

}
