/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class PurchaseItemReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        final int pageId = reader.readInteger();
        final int itemId = reader.readInteger();
        final String extra = reader.readUTF();

        if (Bootloader.getGame().getCatalogManager().getCatalogPages().containsKey(pageId)) {
            Bootloader.getGame().getCatalogManager().purchaseItem(session, Bootloader.getGame().getCatalogManager().getCatalogPages().get(pageId), itemId, extra);
        }
    }

}
