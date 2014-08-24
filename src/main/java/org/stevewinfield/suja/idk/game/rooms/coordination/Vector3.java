/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.coordination;

public class Vector3 {

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getAltitude() {
        return altitude;
    }

    public Vector3(final int x, final int y, final double altitude) {
        this.x = x;
        this.y = y;
        this.altitude = altitude;
    }

    public Vector3() {
        this.x = 0;
        this.y = 0;
        this.altitude = 0;
    }

    @Override
    public String toString() {
        return x + ";" + y + ";" + altitude;
    }

    @Override
    public boolean equals(final Object x) {
        if (x instanceof Vector3) {
            final Vector3 l = (Vector3) x;
            return l.getX() == this.x && l.getY() == this.y && l.getAltitude() == this.altitude;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return (31 + this.x) * 31 + this.y;
    }

    public Vector2 getVector2() {
        return new Vector2(x, y);
    }

    public static Vector3 createFromString(final String position) {
        final String[] bits = position.split(";");

        final int x = Integer.valueOf(bits[0]);
        int y = 0;
        int z = 0;

        if (bits.length > 1) {
            y = Integer.valueOf(bits[1]);
        }

        if (bits.length > 2) {
            z = Integer.valueOf(bits[2]);
        }

        return new Vector3(x, y, z);
    }

    public int calculateRotation(final int new_x, final int new_y) {
        int rot = 0;

        if (x > new_x && y > new_y) {
            rot = 7;
        } else if (x < new_x && y < new_y) {
            rot = 3;
        } else if (x > new_x && y < new_y) {
            rot = 5;
        } else if (x < new_x && y > new_y) {
            rot = 1;
        } else if (x > new_x) {
            rot = 6;
        } else if (x < new_x) {
            rot = 2;
        } else if (y < new_y) {
            rot = 4;
        } else if (y > new_y) {
            rot = 0;
        }

        return rot;
    }

    public int distanceFrom(final Vector3 pos) {
        return (Math.abs(x + pos.getX()) + Math.abs(y + pos.getY()));
    }

    // fields
    private final int x;
    private final int y;
    private final double altitude;
}
