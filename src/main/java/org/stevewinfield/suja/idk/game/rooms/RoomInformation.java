/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

public class RoomInformation {
    // getters
    public int getId() {
        return id;
    }

    public int getRoomType() {
        return roomType;
    }

    public int getOwnerId() {
        return ownerId;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getAccessType() {
        return accessType;
    }

    public String getPassword() {
        return password;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public int getScore() {
        return score;
    }

    public String getModelName() {
        return modelName;
    }

    public boolean isTradingEnabled() {
        return tradingEnabled;
    }

    public RoomModel getModel() {
        return model;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public boolean petsAreAllowed() {
        return allowPets;
    }

    public boolean petsEatingAllowed() {
        return allowPetsEating;
    }

    public boolean blockingDisabled() {
        return disableBlocking;
    }

    public boolean wallsHidden() {
        return hideWalls;
    }

    public int getWallThickness() {
        return wallThickness;
    }

    public int getFloorThickness() {
        return floorThickness;
    }

    public ConcurrentHashMap<String, String> getDecorations() {
        return decorations;
    }

    public int getTotalPlayers() {
        return totalPlayers;
    }

    public String[] getRoomTags() {
        return tags;
    }

    public String[] getSearchableTags() {
        return tags;
    }

    @Override
    public boolean equals(final Object o) {
        if (o == null) {
            return false;
        }
        if (!(o instanceof RoomInformation)) {
            return false;
        }
        if (o == this) {
            return true;
        }

        final RoomInformation a = (RoomInformation) o;
        return this.id == a.id;
    }

    public RoomInformation() {
        this.id = -1;
        this.ownerName = "";
        this.name = "NoName";
        this.description = "Secret >:)";
        this.accessType = 2;
        this.password = "dontcry";
        this.modelName = "";
    }

    public void setDecoration(final String k, final String s) {
        this.decorations.put(k, s);
    }

    public void set(final ResultSet row) throws SQLException {
        this.set(row, "");
    }

    public void set(final String name, final String description, final String[] tags, final int accessType, final String password, final int maxPlayers, final int categoryId, final boolean allowPets, final boolean allowPetsEating, final boolean hideWalls, final int wallThickness, final int floorThickness, final boolean disableBlocking, final boolean tradingEnabled) {
        this.name = name;
        this.description = description;
        this.tags = tags;
        this.accessType = accessType;
        this.password = password;
        this.maxPlayers = maxPlayers;
        this.categoryId = categoryId;
        this.allowPets = allowPets;
        this.allowPetsEating = allowPetsEating;
        this.hideWalls = hideWalls;
        this.wallThickness = wallThickness;
        this.floorThickness = floorThickness;
        this.disableBlocking = disableBlocking;
        this.tradingEnabled = tradingEnabled;
    }

    public void set(final ResultSet row, final String nickname) throws SQLException {
        this.id = row.getInt("id");
        this.tags = row.getString("tags").split(",");
        this.roomType = row.getInt("room_type");
        this.ownerId = row.getInt("owner_id");
        this.ownerName = !nickname.equals("") ? nickname : row.getString("nickname");
        this.name = row.getString("name");
        this.description = row.getString("description");
        this.accessType = row.getInt("access_type");
        this.password = row.getString("password");
        this.categoryId = row.getInt("category_id");
        this.maxPlayers = row.getInt("max_players");
        this.modelName = row.getString("model_name");
        this.model = Bootloader.getGame().getRoomManager().getRoomModel(modelName);
        this.allowPets = row.getInt("allow_pets") == 1;
        this.allowPetsEating = row.getInt("allow_pets_eating") == 1;
        this.disableBlocking = row.getInt("disable_blocking") == 1;
        this.hideWalls = row.getInt("hide_walls") == 1;
        this.wallThickness = row.getInt("wall_thickness");
        this.floorThickness = row.getInt("floor_thickness");
        final String[] splittingMap = row.getString("decorations").split(";");
        this.decorations = new ConcurrentHashMap<>();
        for (final String entry : splittingMap) {
            this.decorations.put(entry.split("=")[0], entry.split("=")[1]);
        }
        RoomCategory category;
        if ((category = Bootloader.getGame().getRoomManager().getRoomCategory(categoryId)) != null) {
            this.tradingEnabled = category.isTradingEnabled();
        }
    }

    public void setScore(final int score) {
        this.score = score;
    }

    // fields
    public int id;
    private int roomType;
    private int ownerId;
    private String ownerName;
    private String name;
    private String description;
    private int accessType;
    private String password;
    private int categoryId;
    private int maxPlayers;
    private int score;
    private String modelName;
    private RoomModel model;
    private boolean allowPets;
    private boolean allowPetsEating;
    private boolean disableBlocking;
    private boolean hideWalls;
    private int wallThickness;
    private int floorThickness;
    private boolean tradingEnabled;
    public int totalPlayers;
    private ConcurrentHashMap<String, String> decorations;
    private String[] tags;

}
