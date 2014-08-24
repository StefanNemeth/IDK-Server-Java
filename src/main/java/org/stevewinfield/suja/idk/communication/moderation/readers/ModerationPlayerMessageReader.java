package org.stevewinfield.suja.idk.communication.moderation.readers;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.Translations;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.miscellaneous.NotifyType;
import org.stevewinfield.suja.idk.network.sessions.Session;

/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
public class ModerationPlayerMessageReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated() || !session.getPlayerInstance().hasRight("moderation_tool")) {
            return;
        }

        final int playerId = reader.readInteger();
        final String message = reader.readUTF();

        if (message.length() < 1) {
            return;
        }

        final Session target = Bootloader.getSessionManager().getAuthenticatedSession(playerId);

        if (target == null) {
            session.sendNotification(NotifyType.MOD_ALERT, Translations.getTranslation("fail_send_message_user_not_online"));
            return;
        }

        target.sendNotification(NotifyType.MOD_ALERT, message);
        Bootloader.getGame().getModerationManager().logAction(session.getPlayerInstance().getInformation().getId(), "Sent a message to " + target.getPlayerInstance().getInformation().getPlayerName() + " (ID: " + target.getPlayerInstance().getInformation().getId() + "): \"" + message.replace("\"", "'") + "\"");
    }

}
