/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class TradeFinalizedWriter extends MessageWriter {

    public TradeFinalizedWriter() {
        super(OperationCodes.getOutgoingOpCode("TradeFinalized"));
    }

}
