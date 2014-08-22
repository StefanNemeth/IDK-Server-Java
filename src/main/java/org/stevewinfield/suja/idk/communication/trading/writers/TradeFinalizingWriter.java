/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class TradeFinalizingWriter extends MessageWriter {

    public TradeFinalizingWriter() {
        super(OperationCodes.getOutgoingOpCode("TradeFinalizing"));

    }

}
