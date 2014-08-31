package org.stevewinfield.suja.idk.dedicated.commands.defaults;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class KickAllCommand implements IDedicatedServerCommand {
    @Override
    public void execute(String[] args, Logger logger) {
        int i = 0;
        for (final Session session : Bootloader.getSessionManager().getSessions()) {
            session.disconnect();
            i++;
        }
        logger.info(i + " User(s) was/were kicked");
    }

    @Override
    public String getName() {
        return "kickAll";
    }
}
