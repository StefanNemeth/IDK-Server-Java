/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class CatalogUpdateWriter extends MessageWriter {

    public CatalogUpdateWriter() {
        super(OperationCodes.getOutgoingOpCode("CatalogUpdate"));
    }

}
