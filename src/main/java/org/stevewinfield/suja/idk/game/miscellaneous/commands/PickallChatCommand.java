/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous.commands;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.communication.inventory.writers.UpdatePlayerInventoryWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandArguments;
import org.stevewinfield.suja.idk.game.miscellaneous.IChatCommand;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

import java.util.List;

public class PickallChatCommand implements IChatCommand {

    @Override
    public String getPermissionCode() {
        return "command_pickall";
    }

    @Override
    public boolean execute(final RoomPlayer player, final ChatCommandArguments arguments) {
        final RoomInstance room = player.getRoom();

        if (room == null || !room.hasRights(player.getSession(), true)) {
            return false;
        }

        final List<RoomItem> items = new GapList<>();

        for (final RoomItem item : room.getRoomItems().values()) {
            if (item.getInteractorId() == FurnitureInteractor.POST_IT) {
                continue;
            }
            room.removeItem(item, player.getSession());
            items.add(item);
        }

        for (final RoomItem item : items) {
            player.getSession().getPlayerInstance().getInventory().addItem(item, null, room.itemHasToUpdate(item.getItemId()));
        }

        player.getSession().writeMessage(new UpdatePlayerInventoryWriter());
        items.clear();
        return true;
    }

}
