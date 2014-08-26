package org.stevewinfield.suja.idk

import org.junit.Before
import org.junit.Test

class SettingsTest {
    Settings settings

    @Before
    void setUp() {
        def properties = new Properties()
        properties.putAll([
                'setting1':'test1',
                'nested.setting.setting1':'test2'
        ])
        settings = new Settings(properties)
    }

    @Test
    void getPropertyTest() {
        assert settings.getProperty("setting1") == "test1"
    }

    @Test
    void multiLevelGetPropertyTest() {
        assert settings.getProperty("nested.setting.setting1") == "test2"
    }

    @Test
    void testPresentDefaultValue() {
        assert settings.getProperty("setting1", "hello") == "test1"
    }

    @Test
    void testAbsentDefaultValue() {
        assert settings.getProperty("setting2", "hello") == "hello"
    }
}
