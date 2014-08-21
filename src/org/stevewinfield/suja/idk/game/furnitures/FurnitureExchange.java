/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class FurnitureExchange {
    private static Logger logger = Logger.getLogger(FurnitureExchange.class);

    public int getFurniId() {
        return furniId;
    }

    public int getChangeCoins() {
        return changeCoins;
    }

    public int getChangePixels() {
        return changePixels;
    }

    public int getChangeExtra() {
        return changeExtra;
    }

    public void set(final ResultSet row) {
        try {
            this.furniId = row.getInt("id");
            this.changeCoins = row.getInt("change_coins");
            this.changePixels = row.getInt("change_pixels");
            this.changeExtra = row.getInt("change_extra");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private int furniId;
    private int changeCoins;
    private int changePixels;
    private int changeExtra;
}
