/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.trading.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class TradeAcceptStateWriter extends MessageWriter {

    public TradeAcceptStateWriter(final int playerId, final boolean accept) {
        super(OperationCodes.getOutgoingOpCode("TradeAcceptState"));
        super.push(playerId);
        super.push(accept);
    }

}
