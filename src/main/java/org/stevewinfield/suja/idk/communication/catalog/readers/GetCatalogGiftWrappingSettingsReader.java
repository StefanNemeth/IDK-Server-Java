/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogGiftWrappingSettingsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetCatalogGiftWrappingSettingsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        session.writeMessage(new CatalogGiftWrappingSettingsWriter(IDK.CATA_GIFTS_MODERN_ENABLED,
        IDK.CATA_GIFTS_MODERN_PRICE, Bootloader.getGame().getCatalogManager().getCatalogModernGiftItems().keySet(),
        IDK.CATA_GIFTS_BOX_COUNT, IDK.CATA_GIFTS_RIBBON_COUNT));
    }

}
