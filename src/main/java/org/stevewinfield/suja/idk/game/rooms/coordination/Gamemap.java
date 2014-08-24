/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.coordination;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomRelativeHeightmapWriter;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomModel;

import java.util.Collection;

public class Gamemap {

    public int getTileState(final int x, final int y) {
        try {
            return floorMap[x][y];
        } catch (final ArrayIndexOutOfBoundsException e) {
            return TileState.CLOSED;
        }
    }

    public double getHeight(final int x, final int y) {
        return heightMap[x][y];
    }

    public int getEffectId(final int x, final int y) {
        return effectMap[x][y];
    }

    public int[][] getTileStates() {
        return floorMap;
    }

    public double[][] getTileHeights() {
        return heightMap;
    }

    public MessageWriter getRelativeMap() {
        return relativeMap;
    }

    public Gamemap(final RoomModel model, final Heightmap heightmap, final Collection<RoomItem> items) {
        this.model = model;
        this.heightmap = heightmap;
        this.floorMap = new int[heightmap.getSizeX()][heightmap.getSizeY()];
        this.heightMap = new double[heightmap.getSizeX()][heightmap.getSizeY()];
        this.effectMap = new int[heightmap.getSizeX()][heightmap.getSizeY()];
        for (int y = 0; y < heightmap.getSizeY(); y++) {
            for (int x = 0; x < heightmap.getSizeX(); x++) {
                this.floorMap[x][y] = heightmap.getTileState(x, y);
                this.heightMap[x][y] = heightmap.getFloorHeight(x, y);
            }
        }
        for (final RoomItem item : items) {
            if (item.getBase().getType().equals(FurnitureType.WALL)) {
                continue;
            }

            for (final Vector2 posAct : item.getAffectedTiles()) {
                RoomItem highestX = null;

                for (final RoomItem yitem : items) {
                    for (final Vector2 posAct2 : yitem.getAffectedTiles()) {
                        if (posAct2.getX() == posAct.getX() && posAct2.getY() == posAct.getY() && (highestX == null || yitem.getAbsoluteHeight() > highestX.getAbsoluteHeight())) {
                            highestX = yitem;
                        }
                    }
                }

                this.updateTile(posAct, highestX.getState());

                final double altitude = highestX.getState() == TileState.SITABLE || highestX.getState() == TileState.LAYABLE ? highestX.getPosition().getAltitude() : highestX.getAbsoluteHeight();

                if (this.heightMap[posAct.getX()][posAct.getY()] < altitude) {
                    this.heightMap[posAct.getX()][posAct.getY()] = altitude;
                }
            }
        }
        this.generateRelativeMap();
    }

    public void generateRelativeMap() {
        final StringBuilder relativeMap = new StringBuilder();
        for (int y = 0; y < this.heightmap.getSizeY(); y++) {
            for (int x = 0; x < this.heightmap.getSizeX(); x++) {
                if (x == this.model.getDoorPosition().getX() && y == this.model.getDoorPosition().getY()) {
                    relativeMap.append((int) this.model.getDoorPosition().getAltitude()).append("");
                    continue;
                }
                if (this.floorMap[x][y] == TileState.CLOSED) {
                    relativeMap.append("x");
                    continue;
                }
                relativeMap.append((int) this.heightmap.getFloorHeight(x, y));
            }
            relativeMap.append((char) 13);
        }
        this.relativeMap = new RoomRelativeHeightmapWriter(relativeMap.toString());
    }

    public void updateTile(final Vector2 oldTile, final Vector2 tile, final int state, final int oldState, final double oldAltitude) {
        this.floorMap[oldTile.getX()][oldTile.getY()] = oldState;
        this.heightMap[oldTile.getX()][oldTile.getY()] = oldAltitude;
        this.floorMap[tile.getX()][tile.getY()] = state;
    }

    public void updateTile(final Vector2 tile, final int state) {
        this.floorMap[tile.getX()][tile.getY()] = state;
    }

    public void setHeight(final Vector2 tile, final double altitude) {
        this.heightMap[tile.getX()][tile.getY()] = altitude;
    }

    // fields
    private final Heightmap heightmap;
    private final int[][] floorMap;
    private final int[][] effectMap;
    private final double[][] heightMap;
    private MessageWriter relativeMap;
    private final RoomModel model;
}
