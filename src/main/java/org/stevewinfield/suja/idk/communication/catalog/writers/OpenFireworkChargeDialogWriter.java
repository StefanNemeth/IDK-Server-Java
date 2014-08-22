/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class OpenFireworkChargeDialogWriter extends MessageWriter {

    public OpenFireworkChargeDialogWriter(final int itemId, final int currentCharges, final int credits,
    final int pixels, final int charges) {
        super(OperationCodes.getOutgoingOpCode("OpenFireworkChargeDialog"));
        super.push(itemId);
        super.push(currentCharges); // current charges
        super.push(credits); // credits
        super.push(pixels); // pixel
        super.push(0);
        super.push(charges); // how much charges
        super.push(0);
    }

}
