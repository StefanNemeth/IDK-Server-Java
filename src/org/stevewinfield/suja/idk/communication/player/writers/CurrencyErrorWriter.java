/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class CurrencyErrorWriter extends MessageWriter {

    public CurrencyErrorWriter(final boolean creditsError, final boolean pixelsError, final boolean extraError) {
        super(OperationCodes.getOutgoingOpCode("CurrencyError"));
        super.push(creditsError);
        super.push(pixelsError);
        super.push(extraError);
    }

}
