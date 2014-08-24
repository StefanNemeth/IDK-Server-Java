/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.recycler.readers;

import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.recycler.writers.CatalogRecyclerConfigWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetRecyclerConfigurationReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        session.writeMessage(new CatalogRecyclerConfigWriter(IDK.CATA_RECYCLER_ENABLED));
    }

}
