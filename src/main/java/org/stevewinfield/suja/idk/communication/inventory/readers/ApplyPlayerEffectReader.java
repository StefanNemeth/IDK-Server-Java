package org.stevewinfield.suja.idk.communication.inventory.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.inventory.writers.AvatarEffectActivatedWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ApplyPlayerEffectReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isInRoom()) {
            return;
        }

        final RoomInstance room = Bootloader.getGame().getRoomManager().getLoadedRoomInstance(session.getRoomId());

        if (room == null) {
            return;
        }

        int effectId = reader.readInteger();

        if (effectId > -1) {
            final PlayerItem item = session.getPlayerInstance().getInventory().getAvatarEffect(effectId);
            if (item == null || !item.getBase().getType().equals(FurnitureType.AVATAR_EFFECT) || Integer.valueOf(item.getFlags().split("" + (char) 10)[1]) < 1) {
                return;
            }
        } else {
            effectId = session.getRoomPlayer().getEffectCache() > 0 ? session.getRoomPlayer().getEffectCache() : 0;
        }

        session.getRoomPlayer().applyEffect(effectId);
    }

}