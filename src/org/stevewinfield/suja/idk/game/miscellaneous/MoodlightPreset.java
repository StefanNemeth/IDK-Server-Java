/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.miscellaneous;

public class MoodlightPreset {
    private String colorCode;
    private boolean wallsOnly;
    private int colorIntensity;

    public String getColorCode() {
        return colorCode;
    }

    public boolean isOnlyBackground() {
        return wallsOnly;
    }

    public int getColorIntensity() {
        return colorIntensity;
    }

    public void setBackgroundOnly(final boolean bgOnly) {
        this.wallsOnly = bgOnly;
    }

    public void setColorCode(final String color) {
        this.colorCode = color;
    }

    public void setColorIntensity(final int intensity) {
        this.colorIntensity = intensity;
    }

    public MoodlightPreset(final String colorCode, final boolean wallsOnly, final int colorIntensity) {
        this.colorCode = colorCode;
        this.wallsOnly = wallsOnly;
        this.colorIntensity = colorIntensity;
    }
}
