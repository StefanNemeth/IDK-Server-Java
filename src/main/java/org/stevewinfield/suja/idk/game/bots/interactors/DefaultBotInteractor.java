/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.bots.interactors;

import org.stevewinfield.suja.idk.game.bots.IBotInteractor;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class DefaultBotInteractor implements IBotInteractor {

    @Override
    public void onLoaded(final RoomInstance room, final RoomPlayer bot) {
    }

    @Override
    public void onLeft(final RoomInstance room, final RoomPlayer bot) {
    }

    @Override
    public void onCycle(final RoomPlayer bot) {
    }

    @Override
    public void onPlayerSays(final RoomPlayer player, final RoomPlayer bot, final ChatMessage message) {
    }

}
