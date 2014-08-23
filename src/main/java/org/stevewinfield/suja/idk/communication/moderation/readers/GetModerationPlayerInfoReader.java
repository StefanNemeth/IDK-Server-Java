/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.moderation.readers;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.Translations;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.moderation.writers.ModerationPlayerInfoWriter;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetModerationPlayerInfoReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(GetModerationPlayerInfoReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.getPlayerInstance().hasRight("moderation_tool"))
            return;

        final int playerId = reader.readInteger();

        PlayerInformation info = null;
        final Session target = Bootloader.getSessionManager().getAuthenticatedSession(playerId);

        if (target == null || !target.isAuthenticated()) {
            try {
                final ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM players WHERE id=" + playerId)
                .executeQuery();
                if (row.next()) {
                    info = new PlayerInformation();
                    info.set(row);
                }
            } catch (final SQLException e) {
                logger.error("SQL Exception", e);
            }

        } else {
            info = target.getPlayerInstance().getInformation();
        }

        if (info == null) {
            session.sendNotification(NotifyType.MOD_ALERT,
            Translations.getTranslation("fail_load_user_information"));
            return;
        }

        session.writeMessage(new ModerationPlayerInfoWriter(info, target));
    }

}
