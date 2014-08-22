/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public interface IChatCommand {
    String getPermissionCode();

    boolean execute(RoomPlayer player, ChatCommandArguments arguments);
}
