/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class TradeInitiatedWriter extends MessageWriter {

    public TradeInitiatedWriter(final int a, final boolean aCanTrade, final int b, final boolean bCanTrade) {
        super(OperationCodes.getOutgoingOpCode("TradeInitiated"));
        super.push(a);
        super.push(aCanTrade);
        super.push(b);
        super.push(bCanTrade);
    }

}
