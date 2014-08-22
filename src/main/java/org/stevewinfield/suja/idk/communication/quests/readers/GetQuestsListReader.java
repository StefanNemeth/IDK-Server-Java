/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.quests.readers;

import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetQuestsListReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        session.sendNotification(NotifyType.STAFF_ALERT,
        "Diese Funktion ist derzeit deaktiviert bzw. wurde noch nicht programmiert.");
    }

}
