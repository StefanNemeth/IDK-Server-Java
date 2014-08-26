package org.stevewinfield.suja.idk.communication.inventory.writers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;

import java.util.Collection;

public class AvatarEffectListWriter extends MessageWriter {

    public AvatarEffectListWriter(final Collection<PlayerItem> effects) {
        super(OperationCodes.getOutgoingOpCode("AvatarEffectList"));
        super.push(effects.size());

        for (final PlayerItem effect : effects) {
            final String[] flags     = effect.getFlags().split("" + (char)10);
            final int      duration  = Integer.valueOf(flags[0]);
            final boolean  activated = Integer.valueOf(flags[1]) > 0;
            final int      quantity  = Integer.valueOf(flags[2]);
                  int      timeLeft  = -1;

            if (activated) {
                timeLeft = (int)(duration - (Bootloader.getTimestamp() - Integer.valueOf(flags[3])));

                if (timeLeft < 0) {
                    timeLeft = 0;
                }
            }

            super.push(effect.getBase().getSpriteId());
            super.push(duration);
            super.push(activated ? quantity - 1 : quantity);
            super.push(timeLeft);
        }
    }

}
