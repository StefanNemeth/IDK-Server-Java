/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

import java.util.Collection;

public class CatalogGiftWrappingSettingsWriter extends MessageWriter {

    public CatalogGiftWrappingSettingsWriter(final boolean modernEnabled, final int modernPrice, final Collection<Integer> baseItems, final int boxCount, final int ribbonCount) {
        super(OperationCodes.getOutgoingOpCode("CatalogGiftWrappingSettings"));
        super.push(modernEnabled);
        super.push(modernPrice);
        super.push(baseItems.size());
        for (final Integer giftId : baseItems) {
            super.push(giftId);
        }
        super.push(boxCount);
        for (int i = 0; i < boxCount; i++) {
            super.push(i);
        }
        super.push(ribbonCount);
        for (int i = 0; i < ribbonCount; i++) {
            super.push(i);
        }
    }

}
