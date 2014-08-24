package org.stevewinfield.suja.idk;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Enumeration;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Translations {
    public static String getTranslation(final String key, Object... args) {
        return String.format(translations.get(key), args);
    }

    public static void loadTranslations() throws IOException {
        translations = new ConcurrentHashMap<>();
        Properties translationsPropertiesFile = new Properties();
        translationsPropertiesFile.load(new FileInputStream("translations.properties"));
        Enumeration<?> e = translationsPropertiesFile.propertyNames();
        while (e.hasMoreElements()) {
            String key = (String) e.nextElement();
            String value = translationsPropertiesFile.getProperty(key);
            translations.put(key, value);
        }
    }

    public static int getCount() {
        return translations.size();
    }

    private static ConcurrentHashMap<String, String> translations;
}
