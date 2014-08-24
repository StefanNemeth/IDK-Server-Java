/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.InfoChatCommand;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.PickallChatCommand;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.caching.RefreshCatalogCommand;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.caching.RefreshFurnitureCommand;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.caching.RefreshRoomCommand;
import org.stevewinfield.suja.idk.game.plugins.GamePlugin;
import org.stevewinfield.suja.idk.game.plugins.PluginInterfaces;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

import javax.script.Invocable;
import java.util.HashMap;
import java.util.Map;

public class ChatCommandHandler {
    private static final Logger logger = Logger.getLogger(ChatCommandHandler.class);
    private final Map<String, IChatCommand> commands;

    public ChatCommandHandler() {
        commands = new HashMap<>();
        commands.put("pickall", new PickallChatCommand());
        commands.put("info", new InfoChatCommand());
        commands.put("refresh_catalog", new RefreshCatalogCommand());
        commands.put("refresh_furniture", new RefreshFurnitureCommand());
        commands.put("refresh_room", new RefreshRoomCommand());
    }

    public void addChatCommand(final GamePlugin plugin, final String cmd, final String permissionCode, final String f) {
        final PluginInterfaces.ChatCommandExecutor executor = ((Invocable) plugin.getScript()).getInterface(plugin.getScript().get(f), PluginInterfaces.ChatCommandExecutor.class);
        if (executor == null) {
            logger.error("Invalid chat command executor for command" + cmd);
            logger.warn("You didn't set the method execute in " + f);
            return;
        }
        addChatCommand(plugin, cmd, new IChatCommand() {
            @Override
            public String getPermissionCode() {
                return permissionCode;
            }

            @Override
            public boolean execute(final RoomPlayer player, final ChatCommandArguments arguments) {
                try {
                    return executor.execute(player, arguments);
                } catch (final Exception e) {
                    logger.error("Plugin Error", e);
                    return false;
                }
            }
        });
    }

    public void addChatCommand(final GamePlugin plugin, final String cmd, IChatCommand chatCommand) {
        commands.put(cmd, chatCommand);
    }

    public boolean commandExists(final String command) {
        return this.commands.containsKey(command);
    }

    public IChatCommand getCommand(final String command) {
        return this.commands.get(command);
    }
}
