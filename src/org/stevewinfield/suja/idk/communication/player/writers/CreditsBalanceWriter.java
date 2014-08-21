/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class CreditsBalanceWriter extends MessageWriter {

    public CreditsBalanceWriter(final int credits) {
        super(OperationCodes.getOutgoingOpCode("CreditsBalance"));
        super.push(credits + ".0");
    }

}
