/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class MoodlightData {
    private boolean enabled;
    private int currentPreset;
    private final Map<Integer, MoodlightPreset> presets;

    public boolean isEnabled() {
        return enabled;
    }

    public int getCurrentPreset() {
        return currentPreset;
    }

    public Map<Integer, MoodlightPreset> getPresets() {
        return presets;
    }

    public void setCurrentPreset(final int preset) {
        this.currentPreset = preset;
    }

    public void setEnabled(final boolean enabled) {
        this.enabled = enabled;
    }

    public MoodlightData(final boolean enabled, final int currentPreset, final Map<Integer, MoodlightPreset> presets) {
        this.enabled = enabled;
        this.currentPreset = currentPreset;
        this.presets = presets;
    }

    public String getFlagData() {
        String flagData = (enabled ? "1" : "0") + "|" + currentPreset + "|";

        for (int i = 1; i <= 3; i++) {
            final MoodlightPreset preset = presets.get(i);

            if (i > 1) {
                flagData += ";";
            }

            flagData += i + "," + preset.getColorCode() + "," + preset.getColorIntensity() + "," + (preset.isOnlyBackground() ? "1" : "0");
        }

        return flagData;
    }

    public String getDisplayData() {
        MoodlightPreset currentPreset = new MoodlightPreset("#000000", false, 255);

        if (presets.containsKey(this.currentPreset)) {
            currentPreset = presets.get(this.currentPreset);
        }

        return (enabled ? "2" : "1") + "," + this.currentPreset + "," + (currentPreset.isOnlyBackground() ? "2" : "1") + "," + currentPreset.getColorCode() + "," + currentPreset.getColorIntensity();
    }

    public static MoodlightData getInstance(final String itemFlags) {
        boolean enabled = false;
        int currentPreset = 1;

        final Map<Integer, MoodlightPreset> presets = new ConcurrentHashMap<Integer, MoodlightPreset>();

        for (int i = 1; i <= 3; i++) {
            presets.put(i, new MoodlightPreset("#000000", false, 255));
        }

        final String[] majors = itemFlags.split("\\|");

        if (majors.length == 3) {
            enabled = (majors[0].equals("1"));
            currentPreset = Integer.valueOf(majors[1]);

            final String[] minors = majors[2].split(";");

            if (minors.length == 3) {
                presets.clear();

                for (final String minor : minors) {
                    final String[] bits = minor.split(",");

                    final int num = Integer.valueOf(bits[0]);
                    final String colorCode = bits[1];
                    final int colorIntensity = Integer.valueOf(bits[2]);
                    final boolean backgroundOnly = (bits[3].equals("1"));

                    presets.put(num, new MoodlightPreset(colorCode, backgroundOnly, colorIntensity));
                }
            }
        }

        return new MoodlightData(enabled, currentPreset, presets);
    }

    public static boolean isValidColor(final String colorCode) {
        switch (colorCode) {
            case "#000000":
            case "#0053F7":
            case "#EA4532":
            case "#82F349":
            case "#74F5F5":
            case "#E759DE":
            case "#F2F851":
                return true;

            default:
                return false;
        }
    }
}
