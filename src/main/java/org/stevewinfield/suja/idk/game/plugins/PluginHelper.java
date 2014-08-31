package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PluginHelper {
    public Object[] getActiveSessions() {
        return Bootloader.getSessionManager().getSessions().toArray();
    }

    public Object[] getRoomPlayers(final RoomInstance room) {
        return room.getRoomPlayers().values().toArray();
    }
}
