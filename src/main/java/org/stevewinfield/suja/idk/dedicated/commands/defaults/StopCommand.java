package org.stevewinfield.suja.idk.dedicated.commands.defaults;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class StopCommand implements IDedicatedServerCommand {

    @Override
    public void execute(String[] args, Logger logger) {
        logger.info("Stopping server...");
        int i = 0;
        for (final Session session : Bootloader.getSessionManager().getSessions()) {
            session.disconnect();
            i++;
        }
        logger.info(i + " User(s) was/were kicked.");
        i = 0;
        for (final RoomInstance room : Bootloader.getGame().getRoomManager().getLoadedRoomInstances()) {
            room.save();
            i++;
        }
        logger.info(i + " Room(s) was/were saved.");
        Bootloader.exitServer(0);
    }

    @Override
    public String getName() {
        return "stop";
    }
}
