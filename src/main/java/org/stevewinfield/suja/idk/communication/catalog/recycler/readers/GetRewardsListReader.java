/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.catalog.recycler.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.catalog.recycler.writers.CatalogRecyclerRewardsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetRewardsListReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        session.writeMessage(new CatalogRecyclerRewardsWriter(Bootloader.getGame().getCatalogManager().getCatalogRecyclerRewards()));
    }

}
