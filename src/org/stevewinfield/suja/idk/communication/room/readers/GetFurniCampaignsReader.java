/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.room.writers.FurniCampaignsWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetFurniCampaignsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.isLoadingRoom() || !session.roomLoadingChecksPassed())
            return;

        session.writeMessage(new FurniCampaignsWriter());
    }

}
