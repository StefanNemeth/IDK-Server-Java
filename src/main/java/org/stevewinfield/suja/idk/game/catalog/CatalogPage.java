/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.catalog;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.catalog.writers.CatalogPageWriter;

public class CatalogPage implements ISerialize {
    private static Logger logger = Logger.getLogger(CatalogPage.class);

    // getters
    public int getId() {
        return id;
    }

    public int getParentId() {
        return parentId;
    }

    public String getCaption() {
        return caption;
    }

    public int getIcon() {
        return icon;
    }

    public int getColor() {
        return color;
    }

    public boolean isVisible() {
        return visible;
    }

    public String getLayout() {
        return layout;
    }

    public String[] getLayoutStrings() {
        return layoutStrings;
    }

    public String[] getContentStrings() {
        return contentStrings;
    }

    public MessageWriter getCachedMessage() {
        return cachedMessage;
    }

    public HashMap<Integer, CatalogItem> getItems() {
        return items;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public CatalogPage() {
        this.id = -1;
        this.parentId = 0;
        this.caption = "";
        this.icon = 0;
        this.color = 0;
        this.visible = false;
        this.layout = "";
        this.layoutStrings = new String[0];
        this.contentStrings = new String[0];
        this.items = new HashMap<Integer, CatalogItem>();
    }

    public void set(final ResultSet row) {
        try {
            this.id = row.getInt("id");
            this.parentId = row.getInt("parent_id");
            this.caption = row.getString("caption");
            this.icon = row.getInt("icon_id");
            this.color = row.getInt("color_id");
            this.visible = row.getInt("visible") == 1;
            this.layout = row.getString("layout");
            this.layoutStrings = row.getString("layout_strings").split("\\|");
            this.contentStrings = row.getString("content_strings").split("\\|");
            this.enabled = row.getInt("enabled") == 1;
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    public void cache() {
        this.cachedMessage = new CatalogPageWriter(this);
    }

    public void addItem(final CatalogItem item) {
        this.items.put(item.getId(), item);
    }

    // fields
    private int id;
    private int parentId;
    private String caption;
    private int icon;
    private int color;
    private boolean visible;
    private String layout;
    private String[] layoutStrings;
    private String[] contentStrings;
    private final HashMap<Integer, CatalogItem> items;
    private MessageWriter cachedMessage;
    private boolean enabled;

    @Override
    public void serialize(final MessageWriter writer) {
        writer.push(this.visible);
        writer.push(this.color);
        writer.push(this.icon);
        writer.push(this.id);
        writer.push(this.caption);

        final List<CatalogPage> subPages = Bootloader.getGame().getCatalogManager().getSubPages(this.id);
        writer.push(subPages.size());

        for (final CatalogPage page : subPages) {
            writer.push(page);
        }

    }
}
