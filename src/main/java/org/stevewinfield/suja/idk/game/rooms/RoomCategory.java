/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class RoomCategory {

    // getters
    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public boolean isStaffCategory() {
        return staffCategory;
    }

    public boolean isTradingEnabled() {
        return tradingEnabled;
    }

    public RoomCategory() {
        this.id = 0;
        this.title = "";
        this.staffCategory = false;
    }

    public void set(final ResultSet row) throws SQLException {
        this.id = row.getInt("id");
        this.title = row.getString("title");
        this.staffCategory = row.getInt("staff_category") == 1;
        this.tradingEnabled = row.getInt("trading_enabled") == 1;
    }

    // fields
    private int id;
    private String title;
    private boolean staffCategory;
    private boolean tradingEnabled;
}
