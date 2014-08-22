/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class RoomTradeCannotInitiateWriter extends MessageWriter {

    public RoomTradeCannotInitiateWriter() {
        super(OperationCodes.getOutgoingOpCode("RoomTradeCannotInitiate"));
    }

}
