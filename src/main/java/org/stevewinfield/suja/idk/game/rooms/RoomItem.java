/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomFloorItemUpdateFlagsWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomWallItemMovedWriter;
import org.stevewinfield.suja.idk.game.furnitures.Furniture;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.furnitures.IFurnitureInteractor;
import org.stevewinfield.suja.idk.game.miscellaneous.MoodlightData;
import org.stevewinfield.suja.idk.game.rooms.coordination.Pathfinder;
import org.stevewinfield.suja.idk.game.rooms.coordination.Pathfinder.Node;
import org.stevewinfield.suja.idk.game.rooms.coordination.Rotation;
import org.stevewinfield.suja.idk.game.rooms.coordination.TileState;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector3;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredManager;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class RoomItem implements ISerialize {
    private static Logger logger = Logger.getLogger(RoomItem.class);

    public int getItemId() {
        return itemId;
    }

    public Furniture getBase() {
        return base;
    }

    public RoomInstance getRoom() {
        return room;
    }

    public Vector3 getPosition() {
        return position;
    }

    public IFurnitureInteractor getInteractor() {
        return interactor;
    }

    public int getInteractorId() {
        return interactorId;
    }

    public String getWallPosition() {
        return wallPosition;
    }

    public double getAbsoluteHeight() {
        return this.getPosition().getAltitude() + this.getBase().getHeight();
    }

    public int getRotation() {
        return rotation;
    }

    public boolean isWiredItem() {
        return this.base.isWiredItem();
    }

    public List<Vector2> getAffectedTiles() {
        final List<Vector2> list = new GapList<Vector2>();
        if (this.rotation == 0 || this.rotation == 2 || this.rotation == 4 || this.rotation == 6) {
            for (int i = 0; i < this.getBase().getWidth(); i++) {
                final int x = this.rotation == 0 || this.rotation == 4 ? this.getPosition().getX() + i : this.getPosition()
                .getX();
                final int y = this.rotation == 2 || this.rotation == 6 ? this.getPosition().getY() + i : this.getPosition()
                .getY();
                for (int j = 0; j < this.getBase().getLength(); j++) {
                    final int xb = this.rotation == 2 || this.rotation == 6 ? x + j : x;
                    final int xn = this.rotation == 0 || this.rotation == 4 ? y + j : y;
                    list.add(new Vector2(xb, xn));
                }
            }
        }
        return list;
    }

    public String getFlags() {
        return flags;
    }

    public int getFlagsState() {
        int x = 0;

        try {
            x = Integer.valueOf(this.flags);
        } catch (final NumberFormatException ex) {
            x = 0;
        }

        return x;
    }

    public String[] getTermFlags() {
        return termFlags;
    }

    public void setFlags(final int data) {
        this.setFlags(data + "");
    }

    public void setFlags(final String data) {
        this.flags = data;
    }

    public ConcurrentHashMap<Integer, Integer> getInteractingPlayers() {
        return this.interactingPlayers;
    }

    public int getState() {
        if (this.getBase().isLayable())
            return TileState.LAYABLE;
        if (this.getBase().isSitable())
            return TileState.SITABLE;
        return this.walkable ? TileState.OPEN : TileState.BLOCKED;
    }

    public RoomItem(final RoomInstance room) {
        this.itemId = 0;
        this.base = null;
        this.position = new Vector3();
        this.interactorId = 0;
        this.interactor = null;
        this.rotation = 0;
        this.flags = "0";
        this.room = room;
        this.walkable = false;
        this.termCycles = 0;
        this.wallPosition = "";
        this.interactingPlayers = new ConcurrentHashMap<Integer, Integer>();
    }

    public RoomItem(final RoomInstance room, final int itemId, final Furniture base, final int interactorId, final String flags) {
        this.itemId = itemId;
        this.base = base;
        this.room = room;
        this.interactorId = interactorId;
        this.interactor = Bootloader.getGame().getFurnitureManager().getInteractor(interactorId);
        this.position = new Vector3();
        this.flags = flags;
        this.interactingPlayers = new ConcurrentHashMap<Integer, Integer>();
        this.walkable = (this.base.getInteractor() == FurnitureInteractor.GATE && this.flags.equals("1"))
        || this.base.isWalkable();
        this.termCycles = 0;
        this.loadTermFlags();
    }

    private void loadTermFlags() {
        if (WiredManager.isWiredItem(base) || this.getBase().getInteractor() == FurnitureInteractor.POST_IT
        || this.getBase().getInteractor() == FurnitureInteractor.MOODLIGHT
        || this.getBase().getInteractor() == FurnitureInteractor.FIREWORK || this.getBase().isGift()
        || this.getBase().getId() == IDK.CATA_RECYCLER_BOX_ID
        || this.getBase().getInteractor() == FurnitureInteractor.TELEPORTER) {
            this.termFlags = flags.split("" + (char) 10);
            this.flags = "";
        }
        if (this.getBase().getInteractor() == FurnitureInteractor.MOODLIGHT) {
            final MoodlightData data = MoodlightData.getInstance(this.getTermFlags()[0]);
            this.flags = data.getDisplayData();
        }
        if ((this.getBase().isGift() || this.getBase().getId() == IDK.CATA_RECYCLER_BOX_ID)
        && this.termFlags.length > 0) {
            this.flags = this.termFlags[0];
        }
        if (this.getBase().getInteractor() == FurnitureInteractor.POST_IT) {
            this.flags = this.termFlags[0];
        }
    }

    public void set(final ResultSet row) {
        try {
            this.itemId = row.getInt("room_items.item_id");
            this.base = Bootloader.getGame().getFurnitureManager().getFurniture(row.getInt("base_item"));
            this.interactorId = row.getInt("special_interactor") > -1 ? row.getInt("special_interactor") : this.base
            .getInteractor();
            this.interactor = Bootloader.getGame().getFurnitureManager().getInteractor(interactorId);
            this.rotation = row.getInt("rotation");
            this.position = new Vector3(row.getInt("position_x"), row.getInt("position_y"),
            row.getDouble("position_altitude"));
            this.wallPosition = row.getString("wall_position");
            final ResultSet _row = Bootloader.getStorage()
            .queryParams("SELECT flag FROM item_flags WHERE item_id=" + this.itemId).executeQuery();
            if (_row != null && _row.next())
                this.flags = _row.getString("flag");
            else
                this.flags = "0";
            this.walkable = (this.base.getInteractor() == FurnitureInteractor.GATE && this.flags.equals("1"))
            || this.base.isWalkable();
            this.loadTermFlags();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    public void setPosition(final Vector3 position, final int rotation) {
        this.position = position;
        this.rotation = rotation;
    }

    public boolean updateState(final boolean x, final boolean checkForPlayers) {
        boolean update = false;
        for (final Vector2 posAct : this.getAffectedTiles()) {
            if (checkForPlayers) {
                final GapList<RoomPlayer> players = room.getRoomPlayersForTile(posAct);
                if (players.size() > 0) {
                    return false;
                }
            }
            RoomItem oldHighest = null;
            for (final RoomItem yitem : room.getRoomItemsForTile(posAct)) {
                if (oldHighest == null || yitem.getAbsoluteHeight() > oldHighest.getAbsoluteHeight())
                    oldHighest = yitem;
            }
            if (oldHighest != null && oldHighest.getItemId() == this.getItemId()) {
                update = true;
                break;
            }
        }
        if (update) {
            this.walkable = x;
            for (final Vector2 posAct : this.getAffectedTiles()) {
                room.getGamemap().updateTile(posAct, this.getState());
            }
        }
        return update;
    }

    public boolean updateState(final boolean x) {
        return this.updateState(x, true);
    }

    public void update(final boolean db, final boolean com) {
        this.update(db, com, null);
    }

    public void update(final boolean db, final boolean com, final Session session) {
        if (db) {
            this.room.addItemToUpdate(this.itemId);
        }
        if (com) {
            MessageWriter message;
            if (this.base.getType().equals(FurnitureType.WALL))
                message = new RoomWallItemMovedWriter(this);
            else
                message = new RoomFloorItemUpdateFlagsWriter(this);
            this.room.writeMessage(message, session);
        }
    }

    public void update() {
        this.update(true, true, null);
    }

    public void update(final boolean b, final Session session) {
        this.update(b, true, session);
    }

    public void update(final Session session) {
        this.update(true, true, session);
    }

    public void update(final boolean b) {
        this.update(b, true, null);
    }

    public void onPlayerWalksOn(final RoomPlayer player, final boolean handleInteractor) {
        if (handleInteractor)
            this.getInteractor().onPlayerWalksOn(player, this);
        this.room.getWiredHandler().onPlayerWalksOnFurni(player, this);
    }

    public void onPlayerWalksOff(final RoomPlayer player, final boolean handleInteractor) {
        if (handleInteractor)
            this.getInteractor().onPlayerWalksOff(player, this);
        this.room.getWiredHandler().onPlayerWalksOffFurni(player, this);
    }

    @Override
    public void serialize(final MessageWriter writer) {
        if (this.getBase().getType().equals(FurnitureType.WALL)) {
            writer.push(this.itemId + "");
            writer.push(this.base.getSpriteId());
            writer.push(this.wallPosition);
            writer.push(this.flags);
            writer.push(false); // TODO -> use button
        } else {
            int secondaryId = 1;
            if (this.base.isGift() && this.termFlags != null && this.termFlags.length > 2) {
                secondaryId = (Integer.valueOf(this.termFlags[1]) * 1000) + Integer.valueOf(this.termFlags[2]);
            }
            writer.push(this.itemId);
            writer.push(this.base.getSpriteId());
            writer.push(this.position.getX());
            writer.push(this.position.getY());
            writer.push(this.rotation); // rotation
            writer.push(String.valueOf(this.position.getAltitude()));
            writer.push(secondaryId); // secondary id box -> schleife
            writer.push(this.flags); // flags
            writer.push(0); // rental expire
            writer.push(false); // TODO -> use button
        }
    }

    public boolean isTouching(final Vector3 pos, final int rotation, final boolean ignoreItemRotation) {
        return isTouching(pos, rotation, ignoreItemRotation, -1);
    }

    public boolean isTouching(final Vector3 pos, final int rotation, final boolean ignoreItemRotation, final double itemAltitude) {
        if (itemAltitude != -1 && (itemAltitude - pos.getAltitude()) >= IDK.ROOM_MAX_WALK_ALTITUDE_DIFFERENCE) {
            return false;
        }
        if (ignoreItemRotation) {
            if (rotation != -1 && rotation != this.getFrontRotation(pos.getVector2()))
                return false;
            if (this.getPosition().getX() == pos.getX() && this.getPosition().getY() == pos.getY()) {
                return true;
            }
            return (pos.getX() == this.getPosition().getX() && pos.getY() == this.getPosition().getY() + 1)
            || (pos.getX() == this.getPosition().getX() - 1 && pos.getY() == this.getPosition().getY() + 1)
            || (pos.getX() == this.getPosition().getX() - 1 && pos.getY() == this.getPosition().getY())
            || (pos.getX() == this.getPosition().getX() + 1 && pos.getY() == this.getPosition().getY() + 1)
            || (pos.getX() == this.getPosition().getX() && pos.getY() == this.getPosition().getY() - 1)
            || (pos.getX() == this.getPosition().getX() + 1 && pos.getY() == this.getPosition().getY() - 1)
            || (pos.getX() == this.getPosition().getX() + 1 && pos.getY() == this.getPosition().getY())
            || (pos.getX() == this.getPosition().getX() - 1 && pos.getY() == this.getPosition().getY() - 1);
        }

        if (rotation != -1 && rotation != this.getFrontRotation())
            return false;

        if (this.getPosition().getX() == pos.getX() && this.getPosition().getY() == pos.getY()) {
            return true;
        }

        if (this.rotation == 2 || this.rotation == 6)
            return pos.getX() == (this.rotation == 2 ? this.getPosition().getX() + 1 : this.getPosition().getX() - 1)
            && pos.getY() >= this.getPosition().getY()
            && pos.getY() < this.getPosition().getY() + this.getBase().getWidth();
        else
            return pos.getY() == (this.rotation == 4 ? this.getPosition().getY() + 1 : this.getPosition().getY() - 1)
            && pos.getX() >= this.getPosition().getX()
            && pos.getX() < this.getPosition().getX() + this.getBase().getLength();

    }

    public boolean isTouching(final Vector3 pos) {
        return isTouching(pos, -1, true, -1);
    }

    public boolean isTouching(final Vector3 pos, final int rotation, final double altitude) {
        return isTouching(pos, rotation, false, altitude);
    }

    public boolean isTouching(final Vector3 pos, final int rotation) {
        return isTouching(pos, rotation, false, -1);
    }

    public Vector2 getFrontPosition(final Vector2 playerPosition) {
        int lowestWay = -1;
        Vector2 lowestPosition = null;

        for (final Vector2 possibility : Pathfinder.DIRECTIONS) {
            if (room.getGamemap().getTileState(position.getX() + possibility.getX(),
            position.getY() + possibility.getY()) == TileState.OPEN) {
                final Vector2 possibilityPosition = new Vector2(position.getX() + possibility.getX(), position.getY()
                + possibility.getY());
                if (playerPosition.getX() == possibilityPosition.getX()
                && playerPosition.getY() == possibilityPosition.getY())
                    return playerPosition;
                Node node = Pathfinder.getPathInArray(room.getGamemap().getTileStates(), room.getGamemap()
                .getTileHeights(), playerPosition, possibilityPosition);
                if (node != null) {
                    int count = 0;
                    while (node != null) {
                        node = node.getParent();
                        count++;
                    }
                    if (count < lowestWay || lowestWay == -1) {
                        lowestWay = count;
                        lowestPosition = possibilityPosition;
                    }
                }
            }
        }

        return lowestPosition;
    }

    public Vector2 getFrontPosition() {
        return getFrontPosition(false);
    }

    public Vector2 getFrontPosition(final boolean onlyFront) {
        int x = this.getPosition().getX();
        int y = this.getPosition().getY();

        if (!this.walkable || onlyFront) {
            if (this.rotation == 2 || this.rotation == 6) {
                x = this.rotation == 2 ? this.getPosition().getX() + 1 : this.getPosition().getX() - 1;
            } else {
                y = this.rotation == 4 ? this.getPosition().getY() + 1 : this.getPosition().getY() - 1;
            }
        }

        return new Vector2(x, y);
    }

    public int getFrontRotation(final Vector2 front) {
        return Rotation.calculate(front.getX(), front.getY(), this.getPosition().getX(), this.getPosition().getY());
    }

    public int getFrontRotation() {
        if (this.getRotation() == 2)
            return 6;
        else if (this.getRotation() == 6)
            return 2;
        else if (this.getRotation() == 0)
            return 4;
        else
            return 0;
    }

    public void requestCycles(final int s) {
        this.cycles = s;
        this.room.queueItemCycle(this);
    }

    public void setWallPosition(final String pos) {
        this.wallPosition = pos;
    }

    public void onCycle() {
        if (this.termCycles >= this.cycles) {
            this.cycles = 0;
            this.termCycles = 0;
            this.interactor.onCycle(this);
            return;
        }
        termCycles++;
        this.room.queueItemCycle(this);
    }

    public void setTermFlags(final String[] x) {
        this.termFlags = x;
    }

    public int getIncementedCounter() {
        return ++this.counter;
    }

    public int getCounter() {
        return this.counter;
    }

    public void setCounter(final int counter) {
        this.counter = counter;
    }

    public Vector2 getNextInfoData() {
        return nextInfoData;
    }

    public void setNextInfoData(final Vector2 pos) {
        this.nextInfoData = pos;
    }

    public RoomPlayer getActingPlayer() {
        return this.actingPlayer;
    }

    public void setActingPlayer(final RoomPlayer pl) {
        this.actingPlayer = pl;
    }

    // fields
    private int itemId;
    private int interactorId;
    private Furniture base;
    private Vector3 position;
    private String wallPosition;
    private int rotation;
    private IFurnitureInteractor interactor;
    private String flags;
    private String[] termFlags;
    private final RoomInstance room;
    private boolean walkable;
    private Vector2 nextInfoData;
    private int cycles;
    private int termCycles;
    private int counter;
    private RoomPlayer actingPlayer;
    private final ConcurrentHashMap<Integer, Integer> interactingPlayers;
}
