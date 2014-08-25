package org.stevewinfield.suja.idk.dedicated.commands.defaults;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand;

public class StopCommand implements IDedicatedServerCommand {

    @Override
    public void execute(String[] args, Logger logger) {
        logger.info("Stopping server...");
        Bootloader.exitServer(0);
    }

    @Override
    public String getName() {
        return "stop";
    }
}
