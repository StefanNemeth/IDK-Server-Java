/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.navigator.writers.RoomCanCreateResultWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class CheckCanCreateRoomReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        session.writeMessage(
                new RoomCanCreateResultWriter(
                        session.getPlayerInstance().getRooms().size() >= IDK.NAV_MAX_ROOMS_PER_PLAYER &&
                                !session.getPlayerInstance().hasRight("unlimited_rooms"),
                        IDK.NAV_MAX_ROOMS_PER_PLAYER
                )
        );
    }

}
