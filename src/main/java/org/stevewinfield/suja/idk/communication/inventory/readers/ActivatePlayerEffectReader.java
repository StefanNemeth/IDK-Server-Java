package org.stevewinfield.suja.idk.communication.inventory.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.inventory.writers.AvatarEffectActivatedWriter;
import org.stevewinfield.suja.idk.communication.inventory.writers.InventoryObjectsWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ActivatePlayerEffectReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final int effectId = reader.readInteger();
        final PlayerItem item = session.getPlayerInstance().getInventory().getAvatarEffect(effectId);

        if (item == null || !item.getBase().getType().equals(FurnitureType.AVATAR_EFFECT)) {
            return;
        }

        final String[] flags = item.getFlags().split("" + (char)10);

        if (Boolean.valueOf(flags[1])) {
            return;
        }

        item.setFlags(
                flags[0] + (char)10 +
                     "1" + (char)10 +
                flags[2] + (char)10 +
                Bootloader.getTimestamp()
        );

        session.getPlayerInstance().getInventory().updateItem(item.getItemId());
        session.writeMessage(new AvatarEffectActivatedWriter(effectId, Integer.valueOf(flags[0])));
    }

}