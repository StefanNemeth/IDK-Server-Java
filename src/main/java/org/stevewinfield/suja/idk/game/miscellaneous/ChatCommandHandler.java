/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.*;
import org.stevewinfield.suja.idk.game.miscellaneous.commands.caching.*;
import org.stevewinfield.suja.idk.game.plugins.GamePlugin;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;

public class ChatCommandHandler {
    private static Logger logger = Logger.getLogger(ChatCommandHandler.class);
    private final Map<String, IChatCommand> commands;

    public ChatCommandHandler() {
        commands = new HashMap<String, IChatCommand>();
        commands.put("pickall", new PickallChatCommand());
        commands.put("info", new InfoChatCommand());
        commands.put("refresh_catalog", new RefreshCatalogCommand());
        commands.put("refresh_furniture", new RefreshFurnitureCommand());
        commands.put("refresh_room", new RefreshRoomCommand());
    }

    public void addChatCommand(final GamePlugin plugin, final String cmd, final String permissionCode, final String f) {
        commands.put(cmd, new IChatCommand() {
            @Override
            public String getPermissionCode() {
                return permissionCode;
            }

            @Override
            public boolean execute(final RoomPlayer player, final ChatCommandArguments arguments) {
                try {
                    final Boolean executed = (Boolean) ((Invocable) plugin.getScript()).invokeMethod(plugin.getScript()
                    .get(f), "execute", player, arguments);
                    if (executed == null) {
                        return true;
                    } else {
                        return executed;
                    }
                } catch (final NoSuchMethodException e) {
                    logger.warn("You didn't set the method execute in " + f);
                    return false;
                } catch (final Exception e) {
                    logger.error("Plugin Error", e);
                    return false;
                }
            }
        });
    }

    public boolean commandExists(final String command) {
        return this.commands.containsKey(command);
    }

    public IChatCommand getCommand(final String command) {
        return this.commands.get(command);
    }
}
