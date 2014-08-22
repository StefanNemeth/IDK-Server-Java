/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.levels;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.log4j.Logger;

public class LevelRight {
    private static Logger logger = Logger.getLogger(LevelRight.class);

    // getters
    public int getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public LevelRight() {
        this.id = 0;
        this.label = "";
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.label = row.getString("right_label");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private int id;
    private String label;

}
