package org.stevewinfield.suja.idk.dedicated.commands.defaults;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand;

import java.io.File;

public class RefreshLocalPluginsCommand implements IDedicatedServerCommand {

    @Override
    public void execute(String[] args, Logger logger) {
        logger.info("Refreshing local plugins...");
        Bootloader.getPluginManager().loadLocalPlugins(new File(IDK.SYSTEM_PLUGINS_PATH));
        Bootloader.exitServer(0);
    }

    @Override
    public String getName() {
        return "refreshLocalPlugins";
    }
}
