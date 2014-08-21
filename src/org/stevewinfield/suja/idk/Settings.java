/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk;

import java.util.Properties;

public class Settings {
    // getters
    public String getProperty(final String key) {
        return this.properties.getProperty(key);
    }

    public String getProperty(final String key, final String defaultValue) {
        return this.properties.getProperty(key, defaultValue);
    }

    public Settings(final Properties properties) {
        this.properties = properties;
    }

    // fields
    private final Properties properties;
}
