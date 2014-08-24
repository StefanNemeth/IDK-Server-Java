/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.communication.ISerialize;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.writers.*;
import org.stevewinfield.suja.idk.game.bots.BotInstance;
import org.stevewinfield.suja.idk.game.bots.BotPhrase;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatType;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;
import org.stevewinfield.suja.idk.game.rooms.coordination.*;
import org.stevewinfield.suja.idk.game.rooms.coordination.Pathfinder.Node;
import org.stevewinfield.suja.idk.network.sessions.Session;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Random;

public class RoomPlayer implements ISerialize {

    public int getVirtualId() {
        return virtualId;
    }

    public RoomInstance getRoom() {
        return room;
    }

    public int getRoomId() {
        return roomId;
    }

    public Vector3 getPosition() {
        return position;
    }

    public Session getSession() {
        return session;
    }

    public boolean needsUpdate() {
        return updateNeeded;
    }

    public PlayerInformation getPlayerInformation() {
        return playerInformation;
    }

    public RoomItem getFloorItem() {
        return floorItem;
    }

    public Vector2 getGoalPosition() {
        return goalPosition;
    }

    public int getEffectId() {
        return effectId;
    }

    public int getHeadRotation() {
        return headRotation;
    }

    public int getHanditemId() {
        return this.handItem;
    }

    public int getRotation() {
        return rotation;
    }

    public int getCurrentTileState() {
        return currentTileState;
    }

    public int getGoalRotation() {
        return goalRotation;
    }

    public HashMap<String, String> getStatusMap() {
        return statusMap;
    }

    public boolean isWalking() {
        return isWalking;
    }

    public boolean isGettingKicked() {
        return gettingKicked;
    }

    public RoomItem getActingItem() {
        return actingItem;
    }

    public int getEffectCache() {
        return effectCache;
    }

    public BotInstance getBotInstance() {
        return botInstance;
    }

    public void setEffectCache(final int cache) {
        this.effectCache = cache;
    }

    public void setKicked(final boolean f) {
        this.gettingKicked = f;
    }

    public int getDanceId() {
        return this.danceId;
    }

    public boolean isBot() {
        return this.bot;
    }

    public RoomPlayer(final int virtualId, final Session session, final int roomId, final RoomInstance room, final Vector3 position, final int rotation) {
        this(virtualId, roomId, room, position, rotation);
        this.session = session;
        this.playerInformation = session.getPlayerInstance().getInformation();
    }

    public void chat(final String message) {
        this.chat(message, ChatType.SAY);
    }

    public void chat(final String message, final int chatType) {
        room.getRoomTask().offerChatMessageAdd(new ChatMessage(this, message, chatType));
    }

    public RoomPlayer(final int virtualId, final int roomId, final RoomInstance room, final Vector3 position, final int rotation) {
        this.virtualId = virtualId;
        this.roomId = roomId;
        this.room = room;
        this.position = position;
        this.statusMap = new LinkedHashMap<>();
        this.headRotation = rotation;
        this.rotation = rotation;
        this.goalRotation = -1;
        this.passedTilesKick = 0;
        this.currentTileState = -1;
    }

    public RoomPlayer(final int virtualId, final BotInstance botData, final int roomId, final RoomInstance room, final Vector3 position, final int rotation) {
        this(virtualId, roomId, room, position, rotation);
        this.botInstance = botData;
        this.bot = true;
    }

    public void addStatus(final String key, final String value) {
        this.statusMap.put(key, value);
    }

    public void removeStatus(final String key) {
        this.statusMap.remove(key);
    }

    public void update() {
        this.updateNeeded = true;
    }

    public void setUpdateNeeded(final boolean needed) {
        this.updateNeeded = needed;
    }

    public void setWalking(final boolean walk) {
        this.isWalking = walk;
    }

    public void setRotation(final int rot) {
        this.rotation = rot;
        this.headRotation = rot;
    }

    public void setBodyRotation(final int rot) {
        this.rotation = rot;
    }

    public void setHeadRotation(final int rot) {
        this.headRotation = rot;
    }

    public void setStepSetted(final boolean f) {
        this.stepSetted = f;
    }

    public boolean stepIsSetted() {
        return this.stepSetted;
    }

    public void whisper(final Session session, final int actorId, final String message) {
        final MessageWriter writer = new RoomChatWriter(actorId, message, 0, ChatType.WHISPER);
        if (this.session == null || session != this.session) {
            session.writeMessage(writer);
        }
        if (!this.isBot()) {
            this.session.writeMessage(writer);
        }
    }

    public void setPosition(final Vector3 x) {
        this.position = x;
    }

    public void setGoalRotation(final int rot) {
        this.goalRotation = rot;
    }

    public void setSettedPosition(final Vector3 x) {
        this.settedPosition = x;
    }

    public Vector3 getSettedPosition() {
        return this.settedPosition;
    }

    public boolean isFrozen() {
        return this.frozen;
    }

    public void setFrozen(final boolean frozen) {
        this.frozen = frozen;
    }

    public void setActingItem(final RoomItem actingItem) {
        this.actingItem = actingItem;
    }

    @Override
    public void serialize(final MessageWriter writer) {
        if (this.isBot()) {
            writer.push(-1);
            writer.push(this.botInstance.getBotName());
            writer.push(this.botInstance.getMission());
            writer.push(this.botInstance.getAvatar());
        } else {
            writer.push(this.playerInformation.getId());
            writer.push(this.playerInformation.getPlayerName());
            writer.push(this.playerInformation.getMission());
            writer.push(this.playerInformation.getAvatar());
        }

        writer.push(this.virtualId);
        writer.push(this.getPosition().getX());
        writer.push(this.getPosition().getY());
        writer.push(String.valueOf(this.getPosition().getAltitude()));
        writer.push(this.isBot() ? 4 : 2);
        writer.push(this.isBot() ? 3 : 1);

        if (!this.isBot()) {
            writer.push(this.playerInformation.getGender() == PlayerInformation.MALE_GENDER ? "m" : "f");
            writer.push(-1);
            writer.push(-1); // group id
            writer.push(-1);
            writer.push("");
            writer.push(this.playerInformation.getScore());
        }
    }

    public boolean moveTo(final Vector2 pos, final boolean ignoreKick, final boolean ignoreBlocking) {
        return this.moveTo(pos, ignoreKick, ignoreBlocking, false);
    }

    public boolean moveTo(Vector2 pos, final boolean ignoreKick, final boolean ignoreBlocking, final boolean isRollerEvent) {
        if ((this.walkingBlocked && !ignoreBlocking) || (this.gettingKicked && !ignoreKick)) {
            return false;
        }

        RoomItem topItem = null;

        for (final RoomItem item : room.getRoomItemsForTile(pos)) {
            if ((topItem == null || item.getAbsoluteHeight() > topItem.getAbsoluteHeight()) && item.getBase().isLayable()) {
                topItem = item;
            }
        }

        if (topItem != null) {
            if (topItem.getRotation() == 0 && pos.getY() != topItem.getPosition().getY()) {
                pos = new Vector2(pos.getX(), pos.getY() - (pos.getY() - topItem.getPosition().getY()));
            } else if (topItem.getRotation() == 2 && pos.getX() != topItem.getPosition().getX()) {
                pos = new Vector2(pos.getX() - (pos.getX() - topItem.getPosition().getX()), pos.getY());
            }
        }

        if (Pathfinder.getPathInArray(room.getGamemap().getTileStates(), room.getGamemap().getTileHeights(), this.getPosition().getVector2(), pos, this.walkingBlocked) == null) {
            return false;
        }

        this.goalPosition = pos;
        this.isWalking = true;
        this.goalRotation = -1;
        this.actingItem = null;
        this.isRollerEvent = isRollerEvent;

        return true;
    }

    public boolean moveTo(final Vector2 pos) {
        return this.moveTo(pos, false, false, false);
    }

    public boolean moveTo(final Vector2 pos, final boolean isRollerEvent) {
        return this.moveTo(pos, false, false, isRollerEvent);
    }

    public void handleVending(final int cycles, final int handItem) {
        if (cycles == 0) {
            this.cycles = 1;
        } else {
            this.frozen = true;
            this.cycles = cycles;
        }
        this.handleVendingId = handItem;
    }

    public void handleVending(final int handItem) {
        this.handleVending(0, handItem);
    }

    public boolean moveTo(final Vector2 pos, final int rot, final RoomItem actingItem) {
        if (this.frozen || this.walkingBlocked) {
            return false;
        }
        if (pos.getX() == this.getPosition().getX() && pos.getY() == this.getPosition().getY() && rot != -1) {
            this.setRotation(rot);
            this.setUpdateNeeded(true);
            actingItem.getInteractor().onTrigger(this, actingItem, 0, this.getRoom().hasRights(this.getSession()));
            return false;
        }
        final boolean x = this.moveTo(pos);
        if (!x) {
            return false;
        }
        this.goalRotation = rot;
        this.actingItem = actingItem;
        return true;
    }

    public void wave() {
        this.room.writeMessage(new RoomPlayerWaveWriter(this.virtualId), this.getSession());
    }

    public void onCarryItem(final int x) {
        this.room.writeMessage(new RoomPlayerCarryItemWriter(this.virtualId, x), this.getSession());
    }

    public void onCycle() {
        if (this.stepIsSetted()) {
            if ((this.room.getInformation().getModel().getDoorPosition().getX() == this.getSettedPosition().getX() &&
                    this.room.getInformation().getModel().getDoorPosition().getY() == this.getSettedPosition().getY()) ||
                    (this.gettingKicked && ++this.passedTilesKick >= 5)) {
                room.removePlayerFromRoom(session, true, this.gettingKicked);
                return;
            }
            this.setPosition(this.getSettedPosition());
            this.setStepSetted(false);
            final RoomItem highestItem = room.getTopItem(this.getPosition().getX(), this.getPosition().getY());
            if (highestItem == null || (this.floorItem != highestItem)) {
                if (this.floorItem != null) {
                    this.floorItem.onPlayerWalksOff(
                            this,
                            floorItem.getBase().getInteractor() != FurnitureInteractor.WF_PLATE && floorItem.getBase().getInteractor() != FurnitureInteractor.BATTLE_BANZAI_PUCK
                    );
                    this.floorItem = null;
                }
                if (highestItem != null) {
                    this.floorItem = highestItem;
                    highestItem.onPlayerWalksOn(
                            this,
                            highestItem.getBase().getInteractor() != FurnitureInteractor.WF_PLATE && highestItem.getBase().getInteractor() != FurnitureInteractor.BATTLE_BANZAI_PUCK
                    );
                }
            }
        }
        if (this.handItemCycle > 0) {
            if (this.handItemTermCycles >= this.handItemCycle) {
                if (this.handItem > 0) {
                    this.handItemTermCycles = 0;
                    this.handItemCycle = 0;
                    this.handItem = 0;
                    this.onCarryItem(0);
                }
            } else {
                this.handItemTermCycles++;
            }
        }
        if (this.cycles > 0) {
            if (this.termCycles >= this.cycles) {
                if (this.handleVendingId > 0) {
                    this.handItem = this.handleVendingId;
                    this.handItemTermCycles = 0;
                    this.danceId = 0;
                    this.onCarryItem(this.handItem);
                    this.handItemCycle = 240;
                    this.frozen = false;
                    this.handleVendingId = 0;
                }
                this.cycles = 0;
                this.termCycles = 0;
            } else {
                termCycles++;
            }
        }
        if (this.isWalking() && !this.isFrozen()) {
            Vector2 point = this.getGoalPosition();
            final Vector2 oldPoint = this.getPosition().getVector2();
            boolean updateMap = false;

            if (!this.isRollerEvent) {
                Node l = (this.getPosition().getX() == this.getGoalPosition().getX() &&
                        this.getPosition().getY() == this.getGoalPosition().getY()) ?
                        null :
                        Pathfinder.getPathInArray(
                                room.getGamemap().getTileStates(),
                                room.getGamemap().getTileHeights(),
                                this.getPosition().getVector2(),
                                this.getGoalPosition(),
                                this.walkingBlocked
                        );

                if (l != null) {
                    while (l.getParent() != null && l.getParent().getParent() != null) {
                        l = l.getParent();
                    }
                }

                point = l == null ? this.getPosition().getVector2() : new Vector2(l.getX(), l.getY());
            } else {
                this.setPosition(new Vector3(point.getX(), point.getY(), this.room.getAltitude(point)));
                updateMap = true;
            }

            if (point.getX() == this.getPosition().getX() && point.getY() == this.getPosition().getY() || updateMap) {
                this.setWalking(false);
                this.removeStatus("mv");
                if (updateMap) {
                    this.setSettedPosition(this.getPosition());
                    this.setStepSetted(true);
                }
                if (this.getPosition().getX() == this.getGoalPosition().getX() && this.getPosition().getY() == this.getGoalPosition().getY()) {
                    if (this.getGoalRotation() != -1) {
                        this.setRotation(this.getGoalRotation());
                        this.setGoalRotation(-1);
                    }
                    if (this.getActingItem() != null) {
                        this.getActingItem().getInteractor().onTrigger(this, this.getActingItem(), 0, this.room.hasRights(this.getSession()));
                        this.setActingItem(null);
                    }
                } else if (this.gettingKicked) {
                    room.removePlayerFromRoom(session, true, true);
                    return;
                } else if (this.getGoalRotation() != -1) {
                    this.setGoalRotation(-1);
                }
                int state;
                if ((state = room.getGamemap().getTileState(this.getPosition().getX(), this.getPosition().getY())) == TileState.SITABLE ||
                        state == (TileState.SITABLE | TileState.PLAYER) || state == (TileState.SITABLE | TileState.BLOCKED)) {
                    RoomItem highestItem = null;
                    for (final RoomItem item : room.getRoomItemsForTile(this.getPosition().getVector2())) {
                        if ((highestItem == null || item.getAbsoluteHeight() > highestItem.getAbsoluteHeight()) && item.getBase().isSitable()) {
                            highestItem = item;
                        }
                    }
                    if (highestItem != null) {
                        this.setRotation(highestItem.getRotation());
                        this.addStatus("sit", String.valueOf(highestItem.getBase().getHeight()));
                    }
                } else if ((state = room.getGamemap().getTileState(this.getPosition().getX(), this.getPosition().getY())) == TileState.LAYABLE ||
                        state == (TileState.LAYABLE | TileState.PLAYER) || state == (TileState.LAYABLE | TileState.BLOCKED)) {
                    final RoomItem highestItem = room.getTopItem(getPosition().getX(), getPosition().getY());
                    if (highestItem != null) {
                        if (highestItem.getRotation() == 2) {
                            if (this.getPosition().getX() != highestItem.getPosition().getX()) {
                                this.setPosition(new Vector3(highestItem.getPosition().getX(), this.getPosition().getY(), this.getPosition().getAltitude()));
                            }
                        } else if (highestItem.getRotation() == 0) {
                            if (this.getPosition().getY() != highestItem.getPosition().getY()) {
                                this.setPosition(new Vector3(this.getPosition().getX(), highestItem.getPosition().getY(), this.getPosition().getAltitude()));
                            }
                        }
                        this.setRotation(highestItem.getRotation());
                        this.addStatus("lay", String.valueOf(highestItem.getBase().getHeight()) + " null");
                    }
                }
            } else {
                updateMap = true;
            }

            if (updateMap) {
                final int nextX = point.getX();
                final int nextY = point.getY();
                if (!this.isRollerEvent) {
                    this.removeStatus("sit");
                    this.removeStatus("lay");
                    this.removeStatus("mv");
                }
                final RoomItem highestItem = room.getTopItem(nextX, nextY);
                if (highestItem == null || (this.floorItem != highestItem)) {
                    if (this.floorItem != null) {
                        if (this.floorItem.getBase().getEffectId() > 0) {
                            this.applyEffect(this.effectCache > 0 ? this.effectCache : 0);
                            if (this.effectCache > 0) {
                                this.effectCache = 0;
                            }
                        }
                        if (floorItem.getBase().getInteractor() == FurnitureInteractor.WF_PLATE || floorItem.getBase().getInteractor() == FurnitureInteractor.BATTLE_BANZAI_PUCK) {
                            floorItem.getInteractor().onPlayerWalksOff(this, floorItem);
                        }
                    }
                    if (highestItem != null) {
                        if (highestItem.getBase().getEffectId() > 0) {
                            if (this.effectId == RoomPlayerEffect.BANZAI_BLUE || this.effectId == RoomPlayerEffect.BANZAI_GREEN || this.effectId == RoomPlayerEffect.BANZAI_ORANGE || this.effectId == RoomPlayerEffect.BANZAI_PINK) {
                                this.effectCache = this.effectId;
                            }
                            this.applyEffect(highestItem.getBase().getEffectId());
                        }
                        if (highestItem.getBase().getInteractor() == FurnitureInteractor.WF_PLATE) {
                            highestItem.getInteractor().onPlayerWalksOn(this, highestItem);
                        } else if (highestItem.getBase().getInteractor() == FurnitureInteractor.BATTLE_BANZAI_PUCK) {
                            System.out.println("HIII");
                            if (this.trigger) {
                                highestItem.getInteractor().onTrigger(this, highestItem, 1, this.getSession() != null && room.hasRights(this.getSession()));
                                // this.trigger = false;
                            } else {
                                highestItem.getInteractor().onPlayerWalksOn(this, highestItem);
                            }
                        }
                    }
                }
                if (!this.isRollerEvent) {
                    final double nextAltitude = this.room.getAltitude(new Vector2(nextX, nextY));
                    this.addStatus("mv", nextX + "," + nextY + "," + String.valueOf(nextAltitude));
                    this.setRotation(Rotation.calculate(this.getPosition().getX(), this.getPosition().getY(), nextX, nextY));
                    this.setStepSetted(true);
                    this.setSettedPosition(new Vector3(nextX, nextY, nextAltitude));
                }
                if (this.currentTileState != -1) {
                    this.getRoom().getGamemap().updateTile(oldPoint, this.currentTileState);
                }
                this.currentTileState = this.getRoom().getGamemap().getTileState(point.getX(), point.getY());
                final boolean oxi = this.currentTileState == TileState.SITABLE || this.currentTileState == TileState.LAYABLE;
                this.getRoom().getGamemap().updateTile(point, this.getRoom().getInformation().blockingDisabled() ? (TileState.PLAYER | (oxi ? this.currentTileState : 0)) : (TileState.BLOCKED | (oxi ? this.currentTileState : 0)));
            }
            this.update();
        }
        if (this.isBot()) {
            if (this.botInstance.isMovingEnabled() && this.botMoveCycles-- == 0) {
                final Vector2 position = this.getPosition().getVector2();
                final GapList<Vector2> randomTiles = new GapList<>();

                for (final Vector2 vector : Pathfinder.DIRECTIONS) {
                    int state;
                    if (((state = this.getRoom().getGamemap().getTileState(position.getX() + vector.getX(), position.getY() + vector.getY())) == TileState.OPEN || state == TileState.SITABLE) && (room.getInformation().getModel().getDoorPosition().getX() != (position.getX() + vector.getX()) || room.getInformation().getModel().getDoorPosition().getY() != position.getY() + vector.getY())) {
                        randomTiles.add(new Vector2(position.getX() + vector.getX(), position.getY() + vector.getY()));
                    }
                }
                if (randomTiles.size() > 0) {
                    this.moveTo(randomTiles.get((new Random()).nextInt(randomTiles.size())));
                }
                this.botMoveCycles = new Random().nextInt(15) + 1;
            }
            if (this.botInstance.getPhrases().size() > 0 && this.botPhraseCylces-- == 0) {
                final BotPhrase phrase = this.botInstance.getPhrases().get(new Random().nextInt(this.botInstance.getPhrases().size()));
                this.chat(phrase.getPhrase(), phrase.isShouted() ? ChatType.SHOUT : ChatType.SAY);
                this.botPhraseCylces = new Random().nextInt(35) + 45;
            }
            if (this.botCycles > 0 && --this.botCycles == 0) {
                this.getBotInstance().getInteractor().onCycle(this);
            }
        }
    }

    public void applyEffect(final int effectId) {
        if (this.effectId == effectId) {
            return;
        }
        this.effectId = effectId;
        this.getRoom().writeMessage(new RoomPlayerEffectWriter(this.getVirtualId(), effectId), null);
    }

    public void dance(int danceId) {
        if (danceId == this.danceId) {
            return;
        }
        if (danceId < 1 || danceId > 4) {
            danceId = 0;
        }
        if (danceId > 1 && !session.getPlayerInstance().hasClub()) {
            danceId = 1;
        }
        this.danceId = danceId;
        this.handItem = 0;
        this.onCarryItem(0);
        this.room.writeMessage(new RoomPlayerDanceWriter(this.virtualId, danceId), session);
    }

    public void setFloorItem(final RoomItem item) {
        this.floorItem = item;
    }

    public void setCurrentTileState(final int tileState) {
        this.currentTileState = tileState;
    }

    public void requestBotCycles(final int cycles) {
        if (!this.isBot()) {
            return;
        }
        this.botCycles = cycles;
    }

    public void setWalkingBlocked(final boolean blocked) {
        if (blocked) {
            this.isWalking = false;
        }
        this.walkingBlocked = blocked;
    }

    // fields
    private final int virtualId;
    private final RoomInstance room;
    private Session session;
    private BotInstance botInstance;
    private boolean bot;
    private final int roomId;
    private boolean updateNeeded;
    private PlayerInformation playerInformation;
    private Vector3 position;
    private final HashMap<String, String> statusMap;
    private int headRotation;
    private int rotation;
    private Vector2 goalPosition;
    private int effectId;
    private boolean isWalking;
    private boolean stepSetted;
    private boolean gettingKicked;
    private boolean trigger;
    private Vector3 settedPosition;
    private int goalRotation;
    private RoomItem actingItem;
    private RoomItem floorItem;
    private int cycles;
    private int botCycles;
    private int botMoveCycles;
    private int botPhraseCylces;
    private int termCycles;
    private boolean frozen;
    private boolean walkingBlocked;
    private int handleVendingId;
    private int handItem;
    private int handItemCycle;
    private int handItemTermCycles;
    private int danceId;
    private int passedTilesKick;
    private int effectCache;
    private int currentTileState;
    private boolean isRollerEvent;
}
