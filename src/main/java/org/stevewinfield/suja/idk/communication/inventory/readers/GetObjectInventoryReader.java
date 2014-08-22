/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.inventory.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.inventory.writers.InventoryObjectsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetObjectInventoryReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        session
        .writeMessage(new InventoryObjectsWriter('S', session.getPlayerInstance().getInventory().getFloorItems()));

        session
        .writeMessage(new InventoryObjectsWriter('I', session.getPlayerInstance().getInventory().getWallItems()));
    }

}
