/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.readers;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.InputFilter;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.sql.PreparedStatement;
import java.sql.SQLException;

public class SetPlayerFigureReader implements IMessageReader {
    private static Logger logger = Logger.getLogger(SetPlayerFigureReader.class);

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated()) {
            return;
        }

        // TODO verfify via regex..

        String gender = reader.readUTF().toLowerCase();
        final String figure = InputFilter.filterString(reader.readUTF());

        if (figure.length() == 0 || figure.equals(session.getPlayerInstance().getInformation().getAvatar())) {
            return;
        }

        if (gender != "m" && gender != "f") {
            gender = "m";
        }

        session.getPlayerInstance().getInformation().setFigure(figure, gender);
        session.sendInformationUpdate();

        try {
            final PreparedStatement std = Bootloader.getStorage().queryParams("UPDATE players SET figurecode=? WHERE id=" + session.getPlayerInstance().getInformation().getId());
            std.setString(1, figure);
            std.execute();
            std.close();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }

        Bootloader.getGame().getAchievementManager().progressAchievement(session, "ACH_AvatarLooks", 1);
    }

}
