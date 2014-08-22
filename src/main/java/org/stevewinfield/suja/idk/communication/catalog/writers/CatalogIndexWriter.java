/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.catalog.CatalogPage;

public class CatalogIndexWriter extends MessageWriter {

    public CatalogIndexWriter(final CatalogPage root) {
        super(OperationCodes.getOutgoingOpCode("CatalogIndex"));
        super.push(root);
        super.push(false); // set a "new" label
    }

}
