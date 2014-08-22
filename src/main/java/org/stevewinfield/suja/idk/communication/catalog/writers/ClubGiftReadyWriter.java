/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class ClubGiftReadyWriter extends MessageWriter {

    public ClubGiftReadyWriter(final int giftsAmount) {
        super(OperationCodes.getOutgoingOpCode("ClubGiftReady"));
        super.push(giftsAmount);
    }

}
