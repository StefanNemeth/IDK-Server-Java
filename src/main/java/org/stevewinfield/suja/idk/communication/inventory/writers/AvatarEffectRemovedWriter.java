package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class AvatarEffectRemovedWriter extends MessageWriter {

    public AvatarEffectRemovedWriter(final int effectId) {
        super(OperationCodes.getOutgoingOpCode("AvatarEffectRemoved"));
        super.push(effectId);
    }

}