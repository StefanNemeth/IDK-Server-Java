/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous.commands;

import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandArguments;
import org.stevewinfield.suja.idk.game.miscellaneous.IChatCommand;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class InfoChatCommand implements IChatCommand {

    @Override
    public String getPermissionCode() {
        return "command_info";
    }

    @Override
    public boolean execute(final RoomPlayer player, final ChatCommandArguments arguments) {
        player.getSession().sendNotification(NotifyType.STAFF_ALERT,
        "Powered by the IDK Project (Build " + IDK.BUILD_NUMBER + ") developed by Steve Winfield and Rhinnodanny.");
        return true;
    }

}
