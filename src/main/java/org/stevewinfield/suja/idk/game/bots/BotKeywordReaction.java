/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.bots;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class BotKeywordReaction {
    public int getBotId() {
        return botId;
    }

    public String[] getResponses() {
        return responses;
    }

    public int[] getDrinks() {
        return drinks;
    }

    public int getChatType() {
        return chatType;
    }

    public BotKeywordReaction() {
        this.responses = new String[0];
        this.drinks = new int[0];
    }

    public void set(final ResultSet row) throws SQLException {
        this.botId = row.getInt("bot_id");
        this.responses = row.getString("responses").split(";");
        this.chatType = row.getInt("chat_type");
        if (row.getString("serving_drink").length() > 0) {
            final String[] drinks = row.getString("serving_drink").split(";");
            this.drinks = new int[drinks.length];
            for (int i = 0; i < drinks.length; i++) {
                this.drinks[i] = Integer.valueOf(drinks[i]);
            }
        }
    }

    private int chatType;
    private int botId;
    private String[] responses;
    private int[] drinks;
}
