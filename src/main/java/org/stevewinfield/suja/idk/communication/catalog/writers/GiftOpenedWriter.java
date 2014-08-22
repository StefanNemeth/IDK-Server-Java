/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;

public class GiftOpenedWriter extends MessageWriter {

    public GiftOpenedWriter(final Furniture baseItem) {
        super(OperationCodes.getOutgoingOpCode("GiftOpened"));
        super.push(baseItem.getType());
        super.push(baseItem.getSpriteId());
        super.push(baseItem.getName());
    }

}
