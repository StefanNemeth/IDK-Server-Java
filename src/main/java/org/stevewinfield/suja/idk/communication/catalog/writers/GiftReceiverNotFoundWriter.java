/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class GiftReceiverNotFoundWriter extends MessageWriter {

    public GiftReceiverNotFoundWriter() {
        super(OperationCodes.getOutgoingOpCode("GiftReceiverNotFound"));
    }

}
