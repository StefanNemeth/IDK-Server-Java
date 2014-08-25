package org.stevewinfield.suja.idk.dedicated.commands.defaults;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand;

public class RefreshFurnitureCommand implements IDedicatedServerCommand {
    @Override
    public void execute(String[] args, Logger logger) {
        Bootloader.getGame().getFurnitureManager().loadCache();
    }

    @Override
    public String getName() {
        return "refreshfurniture";
    }
}
