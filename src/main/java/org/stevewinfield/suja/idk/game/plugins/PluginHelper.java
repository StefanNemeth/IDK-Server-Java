package org.stevewinfield.suja.idk.game.plugins;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PluginHelper {
<<<<<<< HEAD
    public static Object[] getActiveSessions() {
        return Bootloader.getSessionManager().getSessions().toArray();
    }

    public static Object[] getRoomPlayers(final RoomInstance room) {
        return room.getRoomPlayers().values().toArray();
=======
    public static Session[] getActiveSessions() {
        return Bootloader.getSessionManager().getSessions().toArray(new Session[Bootloader.getSessionManager().getSessions().size()]);
    }

    public static RoomPlayer[] getRoomPlayers(final RoomInstance room) {
        return room.getRoomPlayers().values().toArray(new RoomPlayer[room.getRoomPlayers().values().size()]);
>>>>>>> 0e44230d2be7eef69767b51fbb6bb9bd73440e1a
    }

    public static Object[] getRoomItems(final RoomInstance room) {
        return room.getRoomItems().values().toArray();
    }
}
