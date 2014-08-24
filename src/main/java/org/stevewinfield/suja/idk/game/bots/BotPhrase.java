/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.bots;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BotPhrase {
    private static final Logger logger = Logger.getLogger(BotPhrase.class);

    public int getBotId() {
        return botId;
    }

    public String getPhrase() {
        return phrase;
    }

    public boolean isShouted() {
        return shouted;
    }

    public BotPhrase() {
        this.phrase = "";
    }

    public void set(final ResultSet row) {
        try {
            this.botId = row.getInt("bot_id");
            this.phrase = row.getString("phrase");
            this.shouted = row.getInt("is_shouted") == 1;
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    private int botId;
    private String phrase;
    private boolean shouted;
}
