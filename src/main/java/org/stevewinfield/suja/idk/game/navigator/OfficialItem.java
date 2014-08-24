/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.navigator;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OfficialItem {
    private static final Logger logger = Logger.getLogger(OfficialItem.class);

    // getters
    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public int getRoomId() {
        return roomId;
    }

    public boolean isCategory() {
        return category;
    }

    public int getDisplayType() {
        return displayType;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getImageType() {
        return imageType;
    }

    public String getImage() {
        return image;
    }

    public String getBannerLabel() {
        return bannerLabel;
    }

    public boolean autoExpand() {
        return categoryAutoExpand;
    }

    public OfficialItem() {
        this.id = 0;
        this.parentId = 0;
        this.roomId = 0;
        this.category = false;
        this.displayType = 0;
        this.name = "";
        this.description = "";
        this.imageType = 0;
        this.image = "";
        this.bannerLabel = "";
        this.categoryAutoExpand = false;
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.parentId = row.getInt("parent_id");
            this.roomId = row.getInt("room_id");
            this.name = row.getString("name");
            this.description = row.getString("description");
            this.category = row.getInt("is_category") == 1;
            this.displayType = row.getInt("display_type");
            this.imageType = row.getInt("image_type");
            this.image = row.getString("image_src");
            this.bannerLabel = row.getString("banner_label");
            this.categoryAutoExpand = row.getInt("category_autoexpand") == 1;
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private int id;
    private int parentId;
    private int roomId;
    private boolean category;
    private int displayType;
    private String name;
    private String description;
    private int imageType;
    private String image;
    private String bannerLabel;
    private boolean categoryAutoExpand;
}
