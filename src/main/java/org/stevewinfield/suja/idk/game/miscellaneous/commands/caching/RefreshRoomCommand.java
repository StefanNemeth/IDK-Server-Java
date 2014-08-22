/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous.commands.caching;

import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandArguments;
import org.stevewinfield.suja.idk.game.miscellaneous.IChatCommand;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class RefreshRoomCommand implements IChatCommand {

    @Override
    public String getPermissionCode() {
        return "command_refresh_room";
    }

    @Override
    public boolean execute(final RoomPlayer player, final ChatCommandArguments arguments) {
        final RoomInstance room = player.getRoom();

        if (room == null || !room.hasRights(player.getSession(), true))
            return false;

        room.refresh(); // TODO: player reloading
        return true;
    }

}
