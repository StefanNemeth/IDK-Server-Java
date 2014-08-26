package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

public class AvatarEffectAddedWriter extends MessageWriter {

    public AvatarEffectAddedWriter(final int effectId, final int duration) {
        super(OperationCodes.getOutgoingOpCode("AvatarEffectAdded"));
        super.push(effectId);
        super.push(duration);
    }

}