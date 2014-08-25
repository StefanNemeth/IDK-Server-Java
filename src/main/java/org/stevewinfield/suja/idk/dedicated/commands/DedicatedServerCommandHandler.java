package org.stevewinfield.suja.idk.dedicated.commands;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.dedicated.commands.defaults.KickAllCommand;
import org.stevewinfield.suja.idk.dedicated.commands.defaults.RefreshCatalogCommand;
import org.stevewinfield.suja.idk.dedicated.commands.defaults.RefreshFurnitureCommand;
import org.stevewinfield.suja.idk.dedicated.commands.defaults.StopCommand;

import java.util.concurrent.ConcurrentHashMap;

public class DedicatedServerCommandHandler {
    private static final Logger logger = Logger.getLogger(DedicatedServerCommandHandler.class);
    private ConcurrentHashMap<String, IDedicatedServerCommand> commands;

    public DedicatedServerCommandHandler() {
        this.commands = new ConcurrentHashMap<>();
        registerDefaultCommands();
    }

    public void handle(String line) {
        String[] parts = line.split(" ");
        if (parts.length < 1) {
            logger.error("Invalid command");
            return;
        }
        if (!commands.containsKey(parts[0])) {
            logger.error("Command not found");
            return;
        }
        IDedicatedServerCommand cmd = commands.get(parts[0]);
        String[] args = new String[parts.length - 1];
        System.arraycopy(parts, 1, args, 0, parts.length - 1);
        cmd.execute(args, logger);
    }

    public void registerCommand(IDedicatedServerCommand cmd) {
        commands.put(cmd.getName(), cmd);
    }

    private void registerDefaultCommands() {
        registerCommand(new StopCommand());
        registerCommand(new RefreshCatalogCommand());
        registerCommand(new RefreshFurnitureCommand());
        registerCommand(new KickAllCommand());
    }
}
