/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.coordination;

public class Heightmap {

    public int getTileState(final int x, final int y) {
        return tileStates[x][y];
    }

    public double getFloorHeight(final int x, final int y) {
        return floorHeight[x][y];
    }

    public int[][] getTileStates() {
        return tileStates;
    }

    public int getSizeX() {
        return sizeX;
    }

    public int getSizeY() {
        return sizeY;
    }

    public static double getDistance(final int x1, final int y1, final int x2, final int y2) {
        return Math.sqrt(Math.pow(x1 - x2, 2) + Math.pow(y1 - y2, 2));
    }

    public Heightmap(final String data, final Vector3 doorPosition) {
        final String[] lines = data.split("\r\n");

        this.sizeX = lines[0].length();
        this.sizeY = lines.length;
        this.tileStates = new int[sizeX][sizeY];
        this.floorHeight = new double[sizeX][sizeY];

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {

                if (x == doorPosition.getX() && y == doorPosition.getY()) {
                    tileStates[x][y] = TileState.DOOR;
                    floorHeight[x][y] = (int) doorPosition.getAltitude();
                    continue;
                }
                final char value = lines[y].charAt(x);
                tileStates[x][y] = (value == 'x' ? TileState.CLOSED : TileState.OPEN);
                if (value == 'x') {
                    floorHeight[x][y] = 0;
                } else {
                    switch (value) {
                    case '0':
                        floorHeight[x][y] = 0;
                        continue;
                    case '1':
                        floorHeight[x][y] = 1;
                        continue;
                    case '2':
                        floorHeight[x][y] = 2;
                        continue;
                    case '3':
                        floorHeight[x][y] = 3;
                        continue;
                    case '4':
                        floorHeight[x][y] = 4;
                        continue;
                    case '5':
                        floorHeight[x][y] = 5;
                        continue;
                    case '6':
                        floorHeight[x][y] = 6;
                        continue;
                    }
                }
            }
        }
    }

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder();

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                sb.append(tileStates[x][y] == TileState.CLOSED ? "x" : (int) floorHeight[x][y]);
            }
            sb.append((char) 13);
        }

        return sb.toString();
    }

    // fields
    private final int sizeX;
    private final int sizeY;
    private final int[][] tileStates;
    private final double[][] floorHeight;
}
