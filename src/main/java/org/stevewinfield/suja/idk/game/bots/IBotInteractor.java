/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.bots;

import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public interface IBotInteractor {
    void onLoaded(RoomInstance room, RoomPlayer bot);

    void onLeft(RoomInstance room, RoomPlayer bot);

    void onCycle(RoomPlayer bot);

    void onPlayerSays(RoomPlayer player, RoomPlayer bot, ChatMessage message);
}
