/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.levels;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class LevelManager {
    private static Logger logger = Logger.getLogger(LevelManager.class);

    // getters
    public Level getLevel(final int levelId) {
        return this.levels.containsKey(levelId) ? this.levels.get(levelId) : null;
    }

    public List<LevelRight> getSpecialRights(final String name) {
        return this.specialRights.containsKey(name) ? this.specialRights.get(name) : new GapList<LevelRight>();
    }

    public ConcurrentHashMap<Integer, Level> getLevels() {
        return levels;
    }

    public LevelManager() {
        this.levels = new ConcurrentHashMap<Integer, Level>();
        this.specialRights = new ConcurrentHashMap<String, List<LevelRight>>();

        try {
            int highestLevel = 0;
            ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM player_levels").executeQuery();
            while (row.next()) {
                final Level level = new Level();
                level.set(row);
                if (level.getId() > highestLevel) {
                    highestLevel = level.getId();
                }
                this.levels.put(level.getId(), level);
            }
            row.close();
            for (int i = 1; i < highestLevel; i++) {
                if (!this.levels.containsKey(i)) {
                    this.levels.put(i, new Level(i));
                }
            }
            logger.info(this.levels.size() + " Level(s) loaded.");

            row = Bootloader.getStorage().queryParams("SELECT * FROM level_rights").executeQuery();
            int loadedRights = 0;
            while (row.next()) {
                final LevelRight right = new LevelRight();
                right.set(row);
                if (row.getString("exact_name").length() > 0) {
                    if (!this.specialRights.containsKey(row.getString("exact_name"))) {
                        this.specialRights.put(row.getString("exact_name"), new GapList<LevelRight>());
                    }
                    this.specialRights.get(row.getString("exact_name")).add(right);
                } else {
                    for (int i = row.getInt("min_level"); i <= highestLevel; i++) {
                        this.levels.get(i).addRight(right);
                    }
                }
                loadedRights++;
            }
            row.close();
            logger.info(loadedRights + " Level Right(s) loaded.");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    // fields
    private final ConcurrentHashMap<Integer, Level> levels;
    private final ConcurrentHashMap<String, List<LevelRight>> specialRights;
}
