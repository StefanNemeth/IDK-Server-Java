package org.stevewinfield.suja.idk;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

public class Translations {
    public static String getTranslation(final String key, Object... args) {
        if (translations.containsKey(key)) {
            return String.format(translations.get(key), args);
        }
        return String.format(defaultTranslations.get(key), args);
    }

    public static void loadTranslations() throws IOException {
        defaultTranslations = new ConcurrentHashMap<>();
        Properties defaultTranslationsPropertiesFile = new Properties();
        defaultTranslationsPropertiesFile.load(Bootloader.class.getResourceAsStream("/translations.properties"));
        for (String key : defaultTranslationsPropertiesFile.stringPropertyNames()) {
            String value = defaultTranslationsPropertiesFile.getProperty(key);
            defaultTranslations.put(key, value);
        }

        File translationsFile = new File("translations.properties");
        if (!translationsFile.exists()) {
            FileUtils.copyURLToFile(Bootloader.class.getResource("/translations.properties"), translationsFile);
        }
        translations = new ConcurrentHashMap<>();
        Properties translationsPropertiesFile = new Properties();
        translationsPropertiesFile.load(new FileInputStream("translations.properties"));
        for (String key : translationsPropertiesFile.stringPropertyNames()) {
            String value = translationsPropertiesFile.getProperty(key);
            translations.put(key, value);
        }
    }

    public static int getCount() {
        return translations.size();
    }

    private static ConcurrentHashMap<String, String> translations;
    private static ConcurrentHashMap<String, String> defaultTranslations;
}
