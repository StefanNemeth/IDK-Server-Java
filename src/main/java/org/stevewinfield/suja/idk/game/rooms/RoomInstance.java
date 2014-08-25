/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms;

import org.apache.log4j.Logger;
import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.IDK;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.QueuedMessageWriter;
import org.stevewinfield.suja.idk.communication.global.writers.GenericErrorWriter;
import org.stevewinfield.suja.idk.communication.room.writers.*;
import org.stevewinfield.suja.idk.communication.trading.writers.TradeAbortedWriter;
import org.stevewinfield.suja.idk.game.bots.BotInstance;
import org.stevewinfield.suja.idk.game.bots.BotKeywordReaction;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureInteractor;
import org.stevewinfield.suja.idk.game.furnitures.FurnitureType;
import org.stevewinfield.suja.idk.game.messenger.MessengerBuddy;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatType;
import org.stevewinfield.suja.idk.game.navigator.NavigatorList;
import org.stevewinfield.suja.idk.game.rooms.coordination.*;
import org.stevewinfield.suja.idk.game.rooms.tasks.RoomTask;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredHandler;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredManager;
import org.stevewinfield.suja.idk.game.trading.Trade;
import org.stevewinfield.suja.idk.game.trading.TradeManager;
import org.stevewinfield.suja.idk.network.sessions.Session;
import org.stevewinfield.suja.idk.threadpools.WorkerTasks;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;

public class RoomInstance {
    private static final Logger logger = Logger.getLogger(RoomInstance.class);

    public RoomInformation getInformation() {
        return information;
    }

    public GapList<Integer> getVotes() {
        return votes;
    }

    public RoomItem getMoodlight() {
        return moodLight;
    }

    public RoomTask getRoomTask() {
        return roomTask;
    }

    public ConcurrentHashMap<Integer, RoomPlayer> getRoomPlayers() {
        return roomPlayers;
    }

    public ConcurrentHashMap<Integer, RoomItem> getRoomItems() {
        return roomItems;
    }

    public GapList<Integer> getRights() {
        return rights;
    }

    public Gamemap getGamemap() {
        return gameMap;
    }

    public WiredHandler getWiredHandler() {
        return wiredHandler;
    }

    public TradeManager getTradeManager() {
        return tradeManager;
    }

    public boolean guestStickysAreAllowed() {
        return guestStickysAllowed;
    }

    public RoomInstance() {
        this.information = new RoomInformation();
        this.votes = new GapList<>();
        this.roomTask = new RoomTask(this);
        this.roomPlayers = new ConcurrentHashMap<>();
        this.roomItems = new ConcurrentHashMap<>();
        this.topRoomItems = new ConcurrentHashMap<>();
        this.rights = new GapList<>();
        this.itemsToRemove = new GapList<>();
        this.itemsToAdd = new GapList<>();
        this.itemsToUpdate = new GapList<>();
        this.tradeManager = new TradeManager();
        this.wiredHandler = new WiredHandler(this);
        this.topRoomPlayers = new ConcurrentHashMap<>();

        /**
         * Add room task to Thread Pool
         **/
        this.roomTask.setScheduledFuture(WorkerTasks.addTask(this.roomTask, 0, 500, WorkerTasks.getRoomExecutor()));
    }

    public boolean hasRights(final Session session, final boolean ownerRights) {
        final boolean isOwner = session.getPlayerInstance().hasRight("any_room_owner") || session.getPlayerInstance().getInformation().getId() == this.information.getOwnerId();
        return ownerRights ? isOwner : (isOwner || session.getPlayerInstance().hasRight("any_room_rights") || this.rights.contains(session.getPlayerInstance().getInformation().getId()));
    }

    public boolean hasRights(final Session session) {
        return hasRights(session, false);
    }

    public double getAltitude(final Vector2 position) {
        return this.gameMap.getHeight(position.getX(), position.getY());
    }

    public RoomItem getTopItem(final int x, final int y) {
        final int key = x + (y * this.information.getModel().getHeightMap().getSizeX());
        return this.topRoomItems.containsKey(key) && this.topRoomItems.get(key).size() > 0 ? this.topRoomItems.get(key).getLast() : null;
    }

    public void load(final ResultSet set) {
        this.information = new RoomInformation();
            try {
            for (final NavigatorList list : Bootloader.getGame().getNavigatorListManager().getNavigatorLists().values()) {
                final RoomInformation info = list.getInformationEntry(set.getInt("id"));
                if (info != null) {
                    this.information = info;
                    break;
                }
            }
            this.information.set(set);
            ResultSet row = Bootloader.getStorage().queryParams("SELECT player_id FROM room_votes WHERE room_id=" + information.getId()).executeQuery();
            while (row.next()) {
                this.votes.add(row.getInt("player_id"));
            }
            row.close();
            this.information.setScore(this.votes.size());
            row = Bootloader.getStorage().queryParams("SELECT * FROM room_items, items WHERE room_id = " + this.information.getId() + " AND items.id=room_items.item_id").executeQuery();
            while (row.next()) {
                final RoomItem item = new RoomItem(this);
                item.set(row);
                this.roomItems.put(row.getInt("room_items.item_id"), item);
                if (item.getBase().getInteractor() == FurnitureInteractor.MOODLIGHT && this.moodLight == null) {
                    this.moodLight = item;
                }
                if (WiredManager.isWiredItem(item.getBase())) {
                    this.wiredHandler.addItem(item.getPosition().getVector2(), WiredManager.getInstance(item, this, item.getTermFlags()));
                }
                item.getInteractor().onLoaded(this, item);
            }
            row.close();
            this.gameMap = new Gamemap(this.information.getModel(), this.information.getModel().getHeightMap(), this.roomItems.values());
            for (int y = 0; y < this.information.getModel().getHeightMap().getSizeY(); y++) {
                for (int x = 0; x < this.information.getModel().getHeightMap().getSizeX(); x++) {
                    final GapList<RoomItem> items = new GapList<>();
                    for (final RoomItem item : this.roomItems.values()) {
                        if (item == null) {
                            continue;
                        }
                        final List<Vector2> affectedTiles = item.getAffectedTiles();
                        boolean containsPosition = false;
                        for (final Vector2 affectedTile : affectedTiles) {
                            if (affectedTile.getX() == x && affectedTile.getY() == y) {
                                containsPosition = true;
                                break;
                            }
                        }
                        if (containsPosition) {
                            items.add(item);
                        }
                    }
                    Collections.sort(items, new Comparator<RoomItem>() {
                        @Override
                        public int compare(final RoomItem arg0, final RoomItem arg1) {
                            return ((Double) (arg0.getPosition().getAltitude())).compareTo((arg1.getPosition().getAltitude()));
                        }
                    });
                    this.topRoomItems.put(x + (y * this.information.getModel().getHeightMap().getSizeX()), items);
                }
            }
            this.roomItemsInitialized = true;
            row = Bootloader.getStorage().queryParams("SELECT * FROM room_rights WHERE room_id=" + information.getId()).executeQuery();
            while (row.next()) {
                this.rights.add(row.getInt("player_id"));
            }
            row.close();
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
        for (final BotInstance bot : Bootloader.getGame().getBotManager().getBotsByRoomId(this.information.getId())) {
            final RoomPlayer player = new RoomPlayer(virtualPlayerId++, bot, this.information.getId(), this, bot.getStartPosition(), bot.getStartRotation());
            this.roomPlayers.put(player.getVirtualId(), player);
            if (player.getBotInstance().getInteractor() != null) {
                player.getBotInstance().getInteractor().onLoaded(this, player);
            }
        }
    }

    public void onPlayerLeaves(final RoomPlayer player) {
        if (player.getCurrentTileState() != -1) {
            this.gameMap.updateTile(player.getPosition().getVector2(), player.getCurrentTileState());
        }
        if (player.getFloorItem() != null) {
            player.getFloorItem().onPlayerWalksOff(player, true);
        }
        if (player.getEffectId() > 0) {
            final GameTeam team = this.roomTask.getBanzaiTask().getTeam(player.getEffectId());

            if (team != null && team.getGate() != null) {
                int oldTeamPlayers = team.getGate().getFlagsState();

                team.removePlayer(player);
                team.getGate().setFlags(--oldTeamPlayers + "");
                team.getGate().update(false, true);
            }
        }
        Trade trade;
        if ((trade = this.tradeManager.getTrade(player.getPlayerInformation().getId())) != null) {
            tradeManager.stopTrade(player.getPlayerInformation().getId());

            Session targetSession = null;
            final int targetId = player.getPlayerInformation().getId() == trade.getPlayerOne() ? trade.getPlayerTwo() : trade.getPlayerOne();

            for (final RoomPlayer target : this.getRoomPlayers().values()) {
                if (target.getSession() != null && target.getSession().getPlayerInstance().getInformation().getId() == targetId) {
                    targetSession = target.getSession();
                }
            }

            if (targetSession != null && targetSession.getRoomPlayer() != null) {
                targetSession.writeMessage(new TradeAbortedWriter(player.getPlayerInformation().getId()));
                targetSession.getRoomPlayer().removeStatus("trd");
                targetSession.getRoomPlayer().update();
            }
        }
        this.writeMessage(new RoomPlayerRemovedWriter(player.getVirtualId()), null);
        this.getInformation().totalPlayers--;

        for (final MessengerBuddy buddy : player.getSession().getPlayerMessenger().getOnlineBuddies().values()) {
            buddy.getSession().getPlayerMessenger().onStatusChanged(player.getSession().getPlayerInstance().getInformation().getId());
        }
    }

    public MessageWriter getStatusUpdates(final boolean all) {
        final List<RoomPlayer> updatedPlayers = new GapList<>();
        for (final RoomPlayer player : this.roomPlayers.values()) {
            if (!all) {
                if (!player.needsUpdate()) {
                    continue;
                }
                player.setUpdateNeeded(false);
            }
            updatedPlayers.add(player);
        }

        if (updatedPlayers.size() == 0) {
            return null;
        }

        return new RoomPlayerStatusListWriter(updatedPlayers);
    }

    public boolean giveRights(final RoomPlayer player) {
        if (player.isBot() || this.rights.contains(player.getPlayerInformation().getId())) {
            return true; // TODO: Should be false??
        }
        this.rights.add(player.getPlayerInformation().getId());
        player.addStatus("flatctrl", "");
        player.update();
        player.getSession().writeMessage(new RoomRightsWriter());
        Bootloader.getStorage().executeQuery("INSERT INTO room_rights (room_id, player_id) VALUES (" + this.information.getId() + ", " + player.getPlayerInformation().getId() + ")");
        return true;
    }

    public boolean takeRights(final int playerId) {
        if (!this.rights.contains(playerId)) {
            return false;
        }
        this.rights.remove(new Integer(playerId));
        final Session playerSession = Bootloader.getSessionManager().getAuthenticatedSession(playerId);
        if (playerSession != null && playerSession.isInRoom() && playerSession.getRoomId() == this.information.getId()) {
            playerSession.getRoomPlayer().removeStatus("flatctrl");
            playerSession.getRoomPlayer().update();
            playerSession.writeMessage(new RoomRightsRemovedWriter());
        }
        Bootloader.getStorage().executeQuery("DELETE FROM room_rights WHERE player_id=" + playerId + " AND room_id=" + this.information.getId());
        return true;
    }

    public void onPlayerEnters(final RoomPlayer player) {
        final List<RoomPlayer> playersToDisplay = new GapList<>();
        final QueuedMessageWriter queue = new QueuedMessageWriter();
        if (this.hasRights(player.getSession())) {
            player.addStatus("flatctrl", this.hasRights(player.getSession(), true) ? "useradmin" : "");
        }
        player.getSession().setRoomJoined(true);
        player.getSession().setRoomPlayer(player);

        if (player.getSession().isTeleporting()) {
            if (this.getRoomItems().containsKey(player.getSession().getTargetTeleporter())) {
                final RoomItem targetTeleporter = this.getRoomItems().get(player.getSession().getTargetTeleporter());
                if (!targetTeleporter.getInteractingPlayers().containsKey(2)) {
                    player.setWalkingBlocked(true);
                    player.setPosition(targetTeleporter.getPosition());
                    player.setRotation(targetTeleporter.getRotation());
                    targetTeleporter.getInteractingPlayers().put(2, player.getVirtualId());
                    targetTeleporter.setFlags(2);
                    targetTeleporter.update();
                    targetTeleporter.requestCycles(2);
                }
            }
            player.getSession().setTargetTeleporterId(0);
        }

        for (final RoomPlayer disp : this.roomPlayers.values()) {
            playersToDisplay.add(disp);
        }

        queue.push(new RoomPlayerObjectListWriter(playersToDisplay));
        queue.push(new RoomWallsStatusWriter(information.wallsHidden(), information.getWallThickness(), information.getFloorThickness()));
        queue.push(
                new RoomInfoRightsWriter(
                        (information.getRoomType() == RoomType.PRIVATE),
                        information.getId(),
                        information.getOwnerId() == player.getSession().getPlayerInstance().getInformation().getId()
                                || player.getSession().getPlayerInstance().hasRight("any_room_owner"),
                        ""
                )
        );
        queue.push(new RoomInfoWriter(this));

        final MessageWriter updates = this.getStatusUpdates(true);

        if (updates != null) {
            queue.push(updates);
        }

        for (final RoomPlayer disp : this.roomPlayers.values()) {
            if (disp.getEffectId() > 0) {
                queue.push(new RoomPlayerEffectWriter(disp.getVirtualId(), disp.getEffectId()));
            }
            if (disp.getDanceId() > 0) {
                queue.push(new RoomPlayerDanceWriter(disp.getVirtualId(), disp.getDanceId()));
            }
            if (disp.getHanditemId() > 0) {
                queue.push(new RoomPlayerCarryItemWriter(disp.getVirtualId(), disp.getHanditemId()));
            }
        }

        this.writeMessage(new RoomPlayerObjectListWriter(player), null);
        this.getInformation().totalPlayers++;
        player.getSession().writeMessage(queue);
        player.update();
        this.wiredHandler.onPlayerEnters(player);

        for (final MessengerBuddy buddy : player.getSession().getPlayerMessenger().getOnlineBuddies().values()) {
            buddy.getSession().getPlayerMessenger().onStatusChanged(player.getSession().getPlayerInstance().getInformation().getId());
        }
    }

    public void onPlayerChat(final ChatMessage message) {
        if (message == null || message.getSender() == null) {
            return;
        }
        if (this.wiredHandler.onPlayerSays(message.getSender(), message.getMessage())) {
            return;
        }
        final MessageWriter chatWriter = new RoomChatWriter(message.getSender().getVirtualId(), message.getMessage(), 0, message.getChatType());
        this.writeMessage(chatWriter, null);
        if (!message.getSender().isBot()) {
            for (final RoomPlayer player : this.roomPlayers.values()) {
                if (player.isBot() &&
                        player.getBotInstance().getInteractor() != null &&
                        Heightmap.getDistance(
                                message.getSender().getPosition().getX(),
                                message.getSender().getPosition().getY(),
                                player.getPosition().getX(),
                                player.getPosition().getY()
                        ) < 10) {
                    if (message.getChatType() == ChatType.SHOUT && new Random().nextInt(10) % 2 == 0) {
                        player.chat(IDK.BOTS_SHOUT_RESPONSES[new Random().nextInt(IDK.BOTS_SHOUT_RESPONSES.length)], ChatType.SHOUT);
                        continue;
                    }
                    final String[] words = message.getMessage().replace("?", "").replace("!", "").replace(".", "").split(" ");
                    for (String word : words) {
                        word = word.toLowerCase();
                        if (player.getBotInstance().getKeywordReactions().containsKey(word)) {
                            final BotKeywordReaction reaction = player.getBotInstance().getKeywordReactions().get(word);
                            if (reaction.getResponses().length > 0) {
                                final String response = reaction.getResponses()[new Random().nextInt(reaction.getResponses().length)];
                                if (reaction.getChatType() == ChatType.WHISPER) {
                                    player.whisper(message.getSender().getSession(), player.getVirtualId(), response);
                                } else {
                                    player.chat(response, reaction.getChatType());
                                }
                            }
                            if (reaction.getDrinks().length > 0) {
                                message.getSender().handleVending(reaction.getDrinks()[new Random().nextInt(reaction.getDrinks().length)]);
                            }
                            break;
                        }
                    }
                    player.getBotInstance().getInteractor().onPlayerSays(message.getSender(), player, message);
                }
            }
        }
        message.dispose();
    }

    public void addPlayerToRoom(final Session session) {
        if (this.roomTask.isCanceled()) {
            this.roomTask.setCanceled(false);
        }
        this.roomTask.offerRoomPlayerAdd(
                new RoomPlayer(
                        virtualPlayerId++, session, this.information.getId(), this,
                        this.information.getModel().getDoorPosition(), this.information.getModel().getDoorRotation()
                )
        );
        session.setLoadingRoomId(information.getId());
        session.setRoomJoined(true);
    }

    public void removePlayerFromRoom(final Session session, final boolean notifyClient, final boolean kickNotification) {
        if (kickNotification) {
            session.writeMessage(new GenericErrorWriter(4008));
        }
        if (notifyClient) {
            session.writeMessage(new RoomKickedWriter());
        }
        final RoomPlayer player = session.getRoomPlayer();
        this.roomTask.offerRoomPlayerRemove(player);
        session.setLoadingRoomId(0);
        session.setRoomJoined(false);
        session.setRoomPlayer(null);
        this.writeMessage(new RoomPlayerRemovedWriter(player.getVirtualId()), null);
    }

    public void writeMessage(final MessageWriter writer, final Session sender) {
        if (sender != null) {
            sender.writeMessage(writer);
            writer.setSender(sender.getId());
        }
        this.roomTask.offerRoomMessageWriter(writer);
    }

    public void queueModelData(final QueuedMessageWriter queue) {
        queue.push(this.information.getModel().getHeightmapWriter());
        queue.push(this.getGamemap().getRelativeMap());
    }

    public void removeItem(final RoomItem item, final Session session) {
        final boolean isFloorItem = !item.getBase().getType().equals(FurnitureType.WALL);
        final GapList<RoomPlayer> toUpdate = new GapList<>();
        if (isFloorItem) {
            for (final Vector2 posAct : item.getAffectedTiles()) {
                RoomItem oldHighest = null;
                for (final RoomItem yitem : this.getRoomItemsForTile(posAct)) {
                    if (yitem.getItemId() != item.getItemId() && (oldHighest == null || yitem.getAbsoluteHeight() > oldHighest.getAbsoluteHeight())) {
                        oldHighest = yitem;
                    }
                }
                RoomItem checkItem;
                int oldState;
                double oldAltitude;
                if (oldHighest != null) {
                    oldState = oldHighest.getState();
                    oldAltitude = oldHighest.getBase().isLayable() || oldHighest.getBase().isSitable() ? oldHighest.getPosition().getAltitude() : oldHighest.getAbsoluteHeight();
                    checkItem = oldHighest;
                } else {
                    oldState = this.information.getModel().getHeightMap().getTileState(posAct.getX(), posAct.getY());
                    oldAltitude = this.information.getModel().getHeightMap().getFloorHeight(posAct.getX(), posAct.getY());
                    checkItem = item;
                }
                final GapList<RoomItem> items = this.topRoomItems.get(posAct.getX() + (posAct.getY() * this.information.getModel().getHeightMap().getSizeX()));
                for (int i = 0; i < items.size(); ++i) {
                    if (items.get(i).getItemId() == item.getItemId()) {
                        this.topRoomItems.get(posAct.getX() + (posAct.getY() * this.information.getModel().getHeightMap().getSizeX())).remove(i);
                        break;
                    }
                }
                this.gameMap.updateTile(posAct, oldState);
                this.gameMap.setHeight(posAct, oldAltitude);
                final GapList<RoomPlayer> players = this.getRoomPlayersForTile(posAct);
                if (players.size() > 0) {
                    for (final RoomPlayer player : players) {
                        if (player.getPosition().getAltitude() >= checkItem.getPosition().getAltitude()) {
                            if (!toUpdate.contains(player)) {
                                toUpdate.add(player);
                            }
                        }
                    }
                }
            }
            this.wiredHandler.onFloorItemRemoved(item);
        }
        if (item.getBase().getInteractor() == FurnitureInteractor.MOODLIGHT && this.moodLight != null) {
            this.moodLight = null;
        }
        if (this.itemsToAdd.contains(item.getItemId())) {
            this.itemsToAdd.remove(new Integer(item.getItemId()));
        }
        if (this.itemsToUpdate.contains(item.getItemId())) {
            this.itemsToUpdate.remove(new Integer(item.getItemId()));
        }
        if (item.getBase().isWiredItem()) {
            this.wiredHandler.removeItem(item, item.getPosition().getVector2());
        }
        this.roomItems.remove(item.getItemId());
        this.itemsToRemove.add(item.getItemId());
        this.writeMessage(isFloorItem ? new RoomFloorItemRemovedWriter(item.getItemId()) : new RoomWallItemRemovedWriter(item.getItemId()), session);
        if (isFloorItem) {
            this.updateRoomPlayers(toUpdate);
        }
        item.getInteractor().onRemove(session.getRoomPlayer(), item);
    }

    public boolean setFloorItem(final Session session, final RoomItem item, final Vector2 position, final int rotation) {
        return this.setFloorItem(session, item, position, rotation, false, -1);
    }

    public boolean setFloorItem(final Session session, final RoomItem item, final Vector2 position, final int rotation, final boolean update) {
        return this.setFloorItem(session, item, position, rotation, update, -1);
    }

    public boolean setWallItem(final Session session, final RoomItem item, final String data[], final boolean setUpdate) {
        final boolean newItem = !this.roomItems.containsKey(item.getItemId());

        if (data.length != 3 || !data[0].startsWith(":w=") || !data[1].startsWith("l=") || (!data[2].equals("r") && !data[2].equals("l"))) {
            return false;
        }

        final String wBit = data[0].substring(3, data[0].length());
        final String lBit = data[1].substring(2, data[1].length());

        if (!wBit.contains(",") || !lBit.contains(",")) {
            return false;
        }

        final int w1 = Integer.valueOf(wBit.split(",")[0]);
        final int w2 = Integer.valueOf(wBit.split(",")[1]);
        final int l1 = Integer.valueOf(lBit.split(",")[0]);
        final int l2 = Integer.valueOf(lBit.split(",")[1]);

        if (!session.getPlayerInstance().hasRight("hotel_admin") && (w1 < 0 || w2 < 0 || l1 < 0 || l2 < 0 || w1 > 200 || w2 > 200 || l1 > 200 || l2 > 200)) {
            return false;
        }

        item.setWallPosition(":w=" + w1 + "," + w2 + " l=" + l1 + "," + l2 + " " + data[2]);

        if (newItem) {
            if (item.getBase().getInteractor() == FurnitureInteractor.MOODLIGHT) {
                if (this.moodLight != null) {
                    return false;
                }
                this.moodLight = item;
            }
            this.roomItems.put(item.getItemId(), item);
            if (itemsToRemove.contains(item.getItemId())) {
                itemsToRemove.remove(new Integer(item.getItemId()));
            }
            itemsToAdd.add(item.getItemId());
            if (setUpdate) {
                itemsToUpdate.add(item.getItemId());
            }
            this.writeMessage(new RoomWallItemPlacedWriter(item), session);
            item.getInteractor().onLoaded(this, item);
        } else {
            if (!itemsToAdd.contains(item.getItemId()) && !itemsToRemove.contains(item.getItemId())) {
                itemsToAdd.add(item.getItemId());
            }
            final MessageWriter update = new RoomWallItemMovedWriter(item);
            session.writeMessage(update);
            this.writeMessage(update, session);
        }

        return true;
    }

    public boolean setFloorItem(final Session session, final RoomItem item, final Vector2 position, final int rotation, final boolean setUpdate, final int rollerId) {
        return setFloorItem(session, item, position, rotation, setUpdate, rollerId, 0);
    }

    public boolean setFloorItem(final Session session, final RoomItem item, final Vector2 position, final int rotation, final boolean setUpdate, final int rollerId, final double rollerItemAltitude) {
        if (rotation != 2 && rotation != 0 && rotation != 6 && rotation != 4) {
            return false;
        }
        boolean onlyChangeRot = false;
        final boolean newItem = !this.roomItems.containsKey(item.getItemId());
        if (!newItem) {
            if (item.getPosition().getX() == position.getX() && item.getPosition().getY() == position.getY()) {
                if (item.getRotation() == rotation) {
                    return false;
                }
                onlyChangeRot = true;
            }
        }
        if (!onlyChangeRot) {
            if (this.gameMap.getTileState(position.getX(), position.getY()) == TileState.CLOSED || ((item.getBase().getWidth() > 1 || item.getBase().getHeight() > 1 || !item.getBase().isWalkable() || item.getBase().getHeight() > 0.2) && this.getInformation().getModel().getDoorPosition().getX() == position.getX() && this.getInformation().getModel().getDoorPosition().getY() == position.getY())) {
                return false;
            }
        }
        double newAltitude = this.information.getModel().getHeightMap().getFloorHeight(position.getX(), position.getY());
        final int oldX = item.getPosition().getX();
        final int oldY = item.getPosition().getY();
        final int oldRot = item.getRotation();
        final double oldAlt = item.getPosition().getAltitude();
        final GapList<RoomPlayer> toUpdate = new GapList<>();
        RoomItem setItem = null;
        item.setPosition(new Vector3(position.getX(), position.getY(), 0), rotation);
        final List<Vector2> newPos = item.getAffectedTiles();
        item.setPosition(new Vector3(oldX, oldY, oldAlt), oldRot);
        for (final Vector2 posAct : newPos) {
            if (posAct.getX() > information.getModel().getHeightMap().getSizeX() ||
                    posAct.getY() > information.getModel().getHeightMap().getSizeY() ||
                    gameMap.getTileState(posAct.getX(), posAct.getY()) == TileState.CLOSED) {
                return false;
            }
            final GapList<RoomPlayer> players = this.getRoomPlayersForTile(posAct);
            if (players.size() > 0) {
                if (onlyChangeRot && (item.getState() == TileState.SITABLE ||
                        item.getState() == (TileState.SITABLE | TileState.PLAYER) ||
                        item.getState() == (TileState.SITABLE | TileState.BLOCKED))) {
                    for (final RoomPlayer player : players) {
                        if (!toUpdate.contains(player)) {
                            toUpdate.add(player);
                        }
                    }
                } else {
                    return false;
                }
            }
            final GapList<RoomItem> items = this.getRoomItemsForTile(posAct);
            for (final RoomItem xitem : items) {
                if (xitem.getItemId() != item.getItemId()) {
                    if (rollerId < 0) {
                        if (!xitem.getBase().isStackable() || (item.getInteractorId() == FurnitureInteractor.ROLLER &&
                                onlyChangeRot && xitem.getPosition().getAltitude() >= item.getPosition().getAltitude())) {
                            return false;
                        }
                        if (setItem == null || xitem.getAbsoluteHeight() > setItem.getAbsoluteHeight()) {
                            setItem = xitem;
                        }
                    } else if (xitem.getPosition().getAltitude() <= rollerItemAltitude && (setItem == null ||
                            xitem.getPosition().getAltitude() >= setItem.getPosition().getAltitude())) {
                        setItem = xitem;
                    }

                }
            }
            if (rollerId > -1 && setItem != null && (setItem.getPosition().getAltitude() == rollerItemAltitude)) {
                return false;
            }
        }
        if (!newItem) {
            for (final Vector2 posAct : item.getAffectedTiles()) {
                RoomItem oldHighest = null;
                for (final RoomItem yitem : this.getRoomItemsForTile(posAct)) {
                    if (yitem.getItemId() != item.getItemId() && (oldHighest == null || yitem.getAbsoluteHeight() > oldHighest.getAbsoluteHeight())) {
                        oldHighest = yitem;
                    }
                }
                RoomItem checkItem;
                int oldState;
                double oldAltitude;
                if (oldHighest != null) {
                    oldState = oldHighest.getState();
                    oldAltitude = oldHighest.getBase().isLayable() || oldHighest.getBase().isSitable() ? oldHighest.getPosition().getAltitude() : oldHighest.getAbsoluteHeight();
                    checkItem = oldHighest;
                } else {
                    oldState = this.information.getModel().getHeightMap().getTileState(posAct.getX(), posAct.getY());
                    oldAltitude = this.information.getModel().getHeightMap().getFloorHeight(posAct.getX(), posAct.getY());
                    checkItem = item;
                }
                this.gameMap.updateTile(posAct, oldState);
                this.gameMap.setHeight(posAct, oldAltitude);
                final GapList<RoomItem> items = this.topRoomItems.get(posAct.getX() + (posAct.getY() * this.information.getModel().getHeightMap().getSizeX()));
                for (int i = 0; i < items.size(); ++i) {
                    if (items.get(i).getItemId() == item.getItemId()) {
                        this.topRoomItems.get(posAct.getX() + (posAct.getY() * this.information.getModel().getHeightMap().getSizeX())).remove(i);
                        break;
                    }
                }
                final GapList<RoomPlayer> players = this.getRoomPlayersForTile(posAct);
                if (players.size() > 0) {
                    for (final RoomPlayer player : players) {
                        if (player.getPosition().getAltitude() >= checkItem.getPosition().getAltitude()) {
                            if (!toUpdate.contains(player)) {
                                toUpdate.add(player);
                            }
                        }
                    }
                }
            }
        }
        if (setItem != null) {
            newAltitude = (rollerId > -1 && setItem.getInteractorId() != FurnitureInteractor.ROLLER && setItem.getAbsoluteHeight() > rollerItemAltitude)
                    ? setItem.getPosition().getAltitude() : setItem.getAbsoluteHeight();
        }
        if (onlyChangeRot && newAltitude < oldAlt) {
            newAltitude = oldAlt;
        }
        final int state = item.getState();
        for (final Vector2 posAct : newPos) {
            this.gameMap.updateTile(posAct, state);
            if (item.getState() == TileState.SITABLE || item.getState() == TileState.LAYABLE) {
                this.gameMap.setHeight(posAct, newAltitude);
            } else {
                this.gameMap.setHeight(posAct, newAltitude + item.getBase().getHeight());
            }
            this.topRoomItems.get(posAct.getX() + (posAct.getY() * this.information.getModel().getHeightMap().getSizeX())).add(item);
        }
        item.setPosition(new Vector3(position.getX(), position.getY(), newAltitude), rotation);
        if (!newItem) {
            if (!itemsToAdd.contains(item.getItemId()) && !itemsToRemove.contains(item.getItemId())) {
                itemsToAdd.add(item.getItemId());
            }
            if (rollerId > -1) {
                this.writeMessage(new RollerEventWriter(new Vector3(oldX, oldY, oldAlt), item.getPosition(), 0, rollerId, item.getItemId(), true), null);
            } else {
                this.writeMessage(new RoomItemUpdatedWriter(item), session);
            }
            this.updateRoomPlayers(toUpdate);
        } else {
            this.roomItems.put(item.getItemId(), item);
            if (itemsToRemove.contains(item.getItemId())) {
                itemsToRemove.remove(new Integer(item.getItemId()));
            }
            itemsToAdd.add(item.getItemId());
            if (setUpdate) {
                itemsToUpdate.add(item.getItemId());
            }
            this.writeMessage(new RoomFloorItemPlacedWriter(item), session);
            item.getInteractor().onLoaded(this, item);
        }
        if (!item.getBase().isWiredItem()) {
            item.getInteractor().onPlace(session != null ? session.getRoomPlayer() : null, item);
        } else if (!newItem) {
            this.wiredHandler.moveWired(item, new Vector2(oldX, oldY));
        } else {
            this.wiredHandler.addItem(item.getPosition().getVector2(), WiredManager.getInstance(item, this, item.getTermFlags()));
        }
        return true;
    }

    public GapList<RoomItem> getRoomItemsForTile(final Vector2 pos) {
        return this.topRoomItems.get(pos.getX() + (pos.getY() * this.information.getModel().getHeightMap().getSizeX()));
    }

    public boolean roomItemsInitialized() {
        return this.roomItemsInitialized;
    }

    public void updateRoomPlayers(final GapList<RoomPlayer> players) {
        for (final RoomPlayer player : players) {
            final int state = this.gameMap.getTileState(player.getPosition().getX(), player.getPosition().getY());
            player.setPosition(new Vector3(player.getPosition().getX(), player.getPosition().getY(), this.gameMap.getHeight(player.getPosition().getX(), player.getPosition().getY())));
            this.getGamemap().updateTile(player.getPosition().getVector2(), this.getInformation().blockingDisabled() ? TileState.PLAYER : TileState.BLOCKED);
            player.setFloorItem(null);
            player.setCurrentTileState(state);
            player.removeStatus("sit");
            player.removeStatus("lay");
            if (player.getEffectId() > 0) {
                player.applyEffect(0);
                player.applyEffect(player.getEffectCache() > 0 ? player.getEffectCache() : 0);
                if (player.getEffectCache() > 0) {
                    player.setEffectCache(0);
                }
            }
            RoomItem highestItem = this.getTopItem(player.getPosition().getX(), player.getPosition().getY());
            if (highestItem != null) {
                if (state == TileState.SITABLE || state == (TileState.SITABLE | TileState.PLAYER) || state == (TileState.SITABLE | TileState.BLOCKED)) {
                    for (final RoomItem item : this.getRoomItemsForTile(player.getPosition().getVector2())) {
                        if ((item.getAbsoluteHeight() > highestItem.getAbsoluteHeight()) && item.getBase().isSitable()) {
                            highestItem = item;
                        }
                    }
                    player.setRotation(highestItem.getRotation());
                    player.addStatus("sit", String.valueOf(highestItem.getBase().getHeight()));
                }
                final boolean oxi = state == TileState.SITABLE || state == TileState.LAYABLE;
                this.getGamemap().updateTile(
                        player.getPosition().getVector2(),
                        this.getInformation().blockingDisabled() ? (TileState.PLAYER | (oxi ? state : 0)) : (TileState.BLOCKED | (oxi ? state : 0))
                );
                player.setFloorItem(highestItem);
                highestItem.onPlayerWalksOn(player, true);
            }
            player.update();
        }
    }

    public GapList<RoomPlayer> getRoomPlayersForTile(final Vector2 pos) {
        final GapList<RoomPlayer> players = new GapList<>();
        for (final RoomPlayer player : roomPlayers.values()) {
            if (player.getPosition().getX() == pos.getX() &&
                    player.getPosition().getY() == pos.getY() ||
                    (player.stepIsSetted() && player.getSettedPosition().getX() == pos.getX() && player.getSettedPosition().getY() == pos.getY())) {
                players.add(player);
            }
        }
        return players;
    }

    public GapList<RoomPlayer> getRoomPlayersList() {
        return (GapList<RoomPlayer>) roomPlayers.values();
    }

    public void queueItemCycle(final RoomItem item) {
        this.roomTask.offerItemCycle(item);
    }

    public void save() {
        final StringBuilder removeQuery = new StringBuilder();
        for (final Integer removeItem : this.itemsToRemove) {
            removeQuery.append(" OR item_id=").append(removeItem);
        }
        if (removeQuery.length() > 0) {
            Bootloader.getStorage().executeQuery("DELETE FROM room_items WHERE " + removeQuery.toString().substring(4));
        }
        this.itemsToRemove.clear();
        final StringBuilder insertQuery = new StringBuilder();
        for (final Integer addItem : this.itemsToAdd) {
            if (this.roomItems.get(addItem).getBase().getInteractor() == FurnitureInteractor.TELEPORTER) {
                Bootloader.getGame().getRoomManager().getTeleporterCache().remove(addItem);
            }
            insertQuery.append(" ,(")
                    .append(addItem).append(", ").append(this.information.getId()).append(", ")
                    .append(this.roomItems.get(addItem).getPosition().getX()).append(", ")
                    .append(this.roomItems.get(addItem).getPosition().getY()).append(", ")
                    .append(this.roomItems.get(addItem).getRotation()).append(", ")
                    .append(String.valueOf(this.roomItems.get(addItem).getPosition().getAltitude()))
                    .append(", ").append("'").append(this.roomItems.get(addItem).getWallPosition()).append("')");
        }
        final StringBuilder updateQuery = new StringBuilder();
        List<String> flagList = new GapList<>();
        for (final Integer updateItem : this.itemsToUpdate) {
            if (!this.roomItems.containsKey(updateItem)) {
                continue;
            }
            updateQuery.append(" ,(").append(updateItem).append(", ?)");
            String flags = this.roomItems.get(updateItem).getFlags();
            if (this.roomItems.get(updateItem).getTermFlags() != null) {
                flags = "";
                for (final String tFlag : this.roomItems.get(updateItem).getTermFlags()) {
                    flags += tFlag + (char) 10;
                }
            }
            flagList.add(flags);
        }
        if (insertQuery.length() > 0) {
            Bootloader.getStorage().executeQuery("REPLACE INTO room_items (item_id, room_id, position_x, position_y, rotation, position_altitude, wall_position) " +
                    "VALUES " + insertQuery.toString().substring(2));
        }
        if (updateQuery.length() > 0) {
            final PreparedStatement pn = Bootloader.getStorage().queryParams("REPLACE INTO item_flags (item_id, flag) VALUES " + updateQuery.toString().substring(2));
            try {
                for (int i = 0; i < flagList.size(); i++) {
                    pn.setString(i + 1, flagList.get(i));
                }
                pn.execute();
            } catch (final SQLException ex) {
                logger.error("SQL Exception", ex);
            }
            flagList.clear();
        }
        this.itemsToAdd.clear();
        this.itemsToUpdate.clear();
    }

    public void addItemToUpdate(final Integer itemId) {
        this.itemsToUpdate.add(itemId);
    }

    public boolean itemHasToUpdate(final Integer item) {
        return this.itemsToUpdate.contains(item);
    }

    public void refresh() {
        this.roomTask.setCanceled(true);
        this.save();
    }

    public GapList<RoomItem> getFloorItems() {
        final GapList<RoomItem> items = new GapList<>();
        for (final RoomItem item : this.roomItems.values()) {
            if (item != null && !item.getBase().getType().equals(FurnitureType.WALL)) {
                items.add(item);
            }
        }
        return items;
    }

    public GapList<RoomItem> getWallItems() {
        final GapList<RoomItem> items = new GapList<>();
        for (final RoomItem item : this.roomItems.values()) {
            if (item != null && item.getBase().getType().equals(FurnitureType.WALL)) {
                items.add(item);
            }
        }
        return items;
    }

    public void setGuestStickysAllowed(final boolean allowed) {
        this.guestStickysAllowed = allowed;
    }

    private RoomInformation information;
    private Gamemap gameMap;
    private final RoomTask roomTask;
    private final WiredHandler wiredHandler;
    private final TradeManager tradeManager;
    private final GapList<Integer> votes;
    private final GapList<Integer> rights;
    private final ConcurrentHashMap<Integer, RoomPlayer> roomPlayers;
    private final ConcurrentHashMap<Integer, RoomItem> roomItems;
    private final ConcurrentHashMap<Integer, GapList<RoomItem>> topRoomItems;
    private final ConcurrentHashMap<Integer, GapList<RoomPlayer>> topRoomPlayers;
    private RoomItem moodLight;
    private int virtualPlayerId;
    private boolean guestStickysAllowed;
    private boolean roomItemsInitialized;

    // sql queues
    private final GapList<Integer> itemsToAdd;
    private final GapList<Integer> itemsToRemove;
    private final GapList<Integer> itemsToUpdate;
}
