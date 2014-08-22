/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.recycler.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class CatalogRecyclerConfigWriter extends MessageWriter {

    public CatalogRecyclerConfigWriter(final boolean enabled) {
        super(OperationCodes.getOutgoingOpCode("CatalogRecyclerConfig"));
        super.push(enabled);
        super.push(false);
    }

}
