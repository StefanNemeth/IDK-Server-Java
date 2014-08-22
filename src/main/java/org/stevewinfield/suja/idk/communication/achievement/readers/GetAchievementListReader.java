/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.achievement.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.achievement.writers.AchievementListWriter;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetAchievementListReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;
        session.writeMessage(new AchievementListWriter(session.getPlayerInstance().getAchievements(), Bootloader
        .getGame().getAchievementManager().getAchievements().values()));
    }
}
