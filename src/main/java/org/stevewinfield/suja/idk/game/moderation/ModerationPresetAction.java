/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.moderation;

import org.apache.log4j.Logger;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class ModerationPresetAction {
    private static Logger logger = Logger.getLogger(ModerationPresetAction.class);

    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getMessage() {
        return message;
    }

    public boolean isCategory() {
        return category;
    }

    public String getCaption() {
        return caption;
    }

    public ConcurrentHashMap<Integer, ModerationPresetAction> getItems() {
        return subItems;
    }

    public ModerationPresetAction() {
        this.subItems = new ConcurrentHashMap<Integer, ModerationPresetAction>();
        this.caption = "";
        this.message = "";
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.parentId = row.getInt("parent_id");
            this.caption = row.getString("caption");
            this.message = row.getString("message");
        } catch (final SQLException ex) {
            logger.error("SQL Exception", ex);
        }
    }

    public void addSubItem(final ModerationPresetAction item) {
        this.subItems.put(item.getId(), item);
    }

    // fields
    private int id;
    private int parentId;
    private boolean category;
    private String caption;
    private String message;
    private final ConcurrentHashMap<Integer, ModerationPresetAction> subItems;
}
