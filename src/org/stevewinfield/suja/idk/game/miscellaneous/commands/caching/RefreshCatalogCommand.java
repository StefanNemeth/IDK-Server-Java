/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous.commands.caching;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogUpdateWriter;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatCommandArguments;
import org.stevewinfield.suja.idk.game.miscellaneous.IChatCommand;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RefreshCatalogCommand implements IChatCommand {

    @Override
    public String getPermissionCode() {
        return "command_refresh_catalog";
    }

    @Override
    public boolean execute(final RoomPlayer player, final ChatCommandArguments arguments) {
        final RoomInstance room = player.getRoom();

        if (room == null)
            return false;

        Bootloader.getGame().getCatalogManager().loadCache();
        final MessageWriter update = new CatalogUpdateWriter();

        for (final Session session : Bootloader.getSessionManager().getSessions())
            session.writeMessage(update);

        return true;
    }

}
