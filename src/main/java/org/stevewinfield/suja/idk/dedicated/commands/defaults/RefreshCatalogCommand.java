package org.stevewinfield.suja.idk.dedicated.commands.defaults;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogUpdateWriter;
import org.stevewinfield.suja.idk.dedicated.commands.IDedicatedServerCommand;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RefreshCatalogCommand implements IDedicatedServerCommand {
    @Override
    public void execute(String[] args, Logger logger) {
        Bootloader.getGame().getCatalogManager().loadCache();
        final MessageWriter update = new CatalogUpdateWriter();

        for (final Session session : Bootloader.getSessionManager().getSessions()) {
            session.writeMessage(update);
        }
        logger.info("Catalog refreshed");
    }

    @Override
    public String getName() {
        return "refreshCatalog";
    }
}
