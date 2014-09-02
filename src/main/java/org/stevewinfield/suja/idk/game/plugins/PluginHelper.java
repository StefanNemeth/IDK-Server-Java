package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PluginHelper {
    public static Session[] getActiveSessions() {
        return Bootloader.getSessionManager().getSessions().toArray(new Session[Bootloader.getSessionManager().getSessions().size()]);
    }

    public static RoomPlayer[] getRoomPlayers(final RoomInstance room) {
        return room.getRoomPlayers().values().toArray(new RoomPlayer[room.getRoomPlayers().values().size()]);
    }

    public static RoomItem[] getRoomItems(final RoomInstance room) {
        return room.getRoomItems().values().toArray(new RoomItem[room.getRoomItems().values().size()]);
    }
}
