/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class TradeAbortedWriter extends MessageWriter {

    public TradeAbortedWriter(final int playerId) {
        super(OperationCodes.getOutgoingOpCode("TradeAborted"));
        super.push(playerId);
    }

}
