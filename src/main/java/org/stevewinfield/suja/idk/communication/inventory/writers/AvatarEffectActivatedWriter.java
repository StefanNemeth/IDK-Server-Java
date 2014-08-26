package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;

public class AvatarEffectActivatedWriter extends MessageWriter {

    public AvatarEffectActivatedWriter(final int spriteId, final int time) {
        super(OperationCodes.getOutgoingOpCode("AvatarEffectActivated"));
        super.push(spriteId);
        super.push(time);
    }

}
