/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.catalog;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class CatalogClubOffer {
    private static final Logger logger = Logger.getLogger(CatalogClubOffer.class);

    // getters
    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPrice() {
        return price;
    }

    public int getLengthDays() {
        return length;
    }

    public int getLengthSeconds() {
        return length * 86400;
    }

    public int getType() {
        return type;
    }

    public int getLengthMonths() {
        int correctedLength = length;

        return (int) Math.ceil(correctedLength / 31);
    }

    public CatalogClubOffer() {
        this.id = 0;
        this.name = "";
        this.price = 0;
        this.length = 0;
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.name = row.getString("name");
            this.price = row.getInt("cost_credits");
            this.length = row.getInt("length_days");
            this.type = row.getInt("type");
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    // fields
    private int id;
    private String name;
    private int price;
    private int length;
    private int type;
}
