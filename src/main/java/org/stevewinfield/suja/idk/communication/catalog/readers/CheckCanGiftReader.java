/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogCanGiftWriter;
import org.stevewinfield.suja.idk.game.catalog.CatalogPage;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class CheckCanGiftReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final int catalogItemId = reader.readInteger();
        boolean canGift = false;

        for (final CatalogPage page : Bootloader.getGame().getCatalogManager().getCatalogPages().values()) {
            if (page.getItems().containsKey(catalogItemId)) {
                canGift = page.getItems().get(catalogItemId).getBaseItem().isGiftable();
                break;
            }
        }

        session.writeMessage(new CatalogCanGiftWriter(catalogItemId, canGift));
    }

}
