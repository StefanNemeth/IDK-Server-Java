/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.levels;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class LevelRight {
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

    public void set(final ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.label = row.getString("right_label");
    }

    // fields
    private int id;
    private String label;

}
