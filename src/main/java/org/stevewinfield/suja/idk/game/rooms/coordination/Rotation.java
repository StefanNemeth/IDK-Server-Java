/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.coordination;

public class Rotation {
    public static int calculate(final int X1, final int Y1, final int X2, final int Y2) {
        int rotation = 0;

        if (X1 > X2 && Y1 > Y2) {
            rotation = 7;
        } else if (X1 < X2 && Y1 < Y2) {
            rotation = 3;
        } else if (X1 > X2 && Y1 < Y2) {
            rotation = 5;
        } else if (X1 < X2 && Y1 > Y2) {
            rotation = 1;
        } else if (X1 > X2) {
            rotation = 6;
        } else if (X1 < X2) {
            rotation = 2;
        } else if (Y1 < Y2) {
            rotation = 4;
        } else if (Y1 > Y2) {
            rotation = 0;
        }

        return rotation;
    }
}
