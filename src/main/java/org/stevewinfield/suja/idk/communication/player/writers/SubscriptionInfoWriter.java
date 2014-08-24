/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class SubscriptionInfoWriter extends MessageWriter {

    public SubscriptionInfoWriter(final String productName, final int clubDays, final int purchaseStatus, final boolean vip, final boolean showPromo, final int regular, final int priceNow) {
        super(OperationCodes.getOutgoingOpCode("SubscriptionInfo"));
        super.push(productName);
        super.push(clubDays);
        super.push(0);
        super.push(0);
        super.push(purchaseStatus);
        super.push(false);
        super.push(vip);
        super.push(0);
        super.push(0);
        super.push(showPromo);
        super.push(regular);
        super.push(priceNow);
    }

}
