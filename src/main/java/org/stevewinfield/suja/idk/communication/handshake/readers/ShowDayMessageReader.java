/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.handshake.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class ShowDayMessageReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }
        if (Bootloader.getSettings().getProperty("idk.game.dayMessageEnabled", "false").equals("true")) {
            session
                    .sendNotification(
                            NotifyType.MULTI_ALERT,
                            Bootloader.getSettings().getProperty("idk.game.dayMessage")
                                    .replace("%user%",
                                            session.getPlayerInstance().getInformation().getPlayerName()
                                    )
                    );
        }
    }

}
