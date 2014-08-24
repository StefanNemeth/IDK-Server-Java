/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.tasks;

import org.magicwerk.brownies.collections.GapList;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.room.writers.RoomPlayerStatusListWriter;
import org.stevewinfield.suja.idk.game.miscellaneous.ChatMessage;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.tasks.games.BattleBanzaiTask;
import org.stevewinfield.suja.idk.game.rooms.wired.WiredDelayEvent;
import org.stevewinfield.suja.idk.threadpools.ServerTask;

import java.util.List;
import java.util.Queue;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ScheduledFuture;

public class RoomTask extends ServerTask {
    private final RoomInstance room;
    private int updates;
    private int emptyRoomCounter;
    private final Queue<RoomPlayer> playersToAdd;
    private final Queue<RoomPlayer> playersToRemove;
    private final Queue<MessageWriter> roomMessageWriters;
    private final Queue<ChatMessage> chatMessages;
    private final Queue<RoomItem> itemCycleRequests;
    private final Queue<WiredDelayEvent> wiredItemDelay;
    private ScheduledFuture<?> scheduledFuture;
    private final GameTask banzaiTask;
    private boolean canceled;

    public RoomTask(final RoomInstance room) {
        this.room = room;
        this.playersToAdd = new ArrayBlockingQueue<RoomPlayer>(1024);
        this.playersToRemove = new ArrayBlockingQueue<RoomPlayer>(1024);
        this.roomMessageWriters = new ArrayBlockingQueue<MessageWriter>(1024);
        this.chatMessages = new ArrayBlockingQueue<ChatMessage>(1024);
        this.itemCycleRequests = new ArrayBlockingQueue<RoomItem>(1024);
        this.wiredItemDelay = new ArrayBlockingQueue<WiredDelayEvent>(1024);
        this.updates = 0;
        this.emptyRoomCounter = 0;
        this.canceled = false;
        this.banzaiTask = new BattleBanzaiTask(room);
    }

    public void offerRoomPlayerAdd(final RoomPlayer player) {
        this.playersToAdd.offer(player);
    }

    public void offerWiredItemDelay(final WiredDelayEvent item) {
        this.wiredItemDelay.offer(item);
    }

    public GameTask getBanzaiTask() {
        return banzaiTask;
    }

    public boolean isCanceled() {
        return canceled;
    }

    public void setCanceled(final boolean c) {
        this.canceled = c;
    }

    public void offerChatMessageAdd(final ChatMessage message) {
        this.chatMessages.offer(message);
    }

    public void offerRoomPlayerRemove(final RoomPlayer player) {
        this.playersToRemove.offer(player);
    }

    public void offerRoomMessageWriter(final MessageWriter writer) {
        this.roomMessageWriters.offer(writer);
    }

    public void offerItemCycle(final RoomItem item) {
        this.itemCycleRequests.offer(item);
    }

    public void setScheduledFuture(final ScheduledFuture<?> scheduledFuture) {
        this.scheduledFuture = scheduledFuture;
    }

    @Override
    public void run() {
        if (this.canceled) {
            if (this.scheduledFuture == null) {
                return;
            }
            for (final RoomPlayer player : this.room.getRoomPlayers().values()) {
                if (player.getSession() != null) {
                    room.removePlayerFromRoom(player.getSession(), true, false);
                }
            }
            this.scheduledFuture.cancel(true);

            room.getInformation().totalPlayers = 0;
            Bootloader.getGame().getNavigatorListManager().setRoom(room.getInformation(), 0, true);
            Bootloader.getGame().getRoomManager().removeLoadedRoomInstance(room.getInformation().getId());
            return;
        }

        if (this.emptyRoomCounter >= 60) {
            this.room.save();
            this.canceled = true;
            this.emptyRoomCounter = 0;
            return;
        }
        if (this.room.getInformation().getTotalPlayers() == 0) {
            this.emptyRoomCounter++;
        }
        final GapList<MessageWriter> messages = new GapList<MessageWriter>();
        final List<RoomPlayer> updatedPlayers = new GapList<RoomPlayer>();
        for (final RoomPlayer player : room.getRoomPlayers().values()) {
            player.onCycle();
            if (player.needsUpdate()) {
                updatedPlayers.add(player);
                player.setUpdateNeeded(false);
            }
        }
        while (!this.chatMessages.isEmpty()) {
            this.room.onPlayerChat(this.chatMessages.poll());
        }
        final List<RoomItem> toCycle = new GapList<RoomItem>();
        while (!itemCycleRequests.isEmpty()) {
            toCycle.add(itemCycleRequests.poll());
        }
        for (final RoomItem item : toCycle) {
            item.onCycle();
        }
        while (!this.roomMessageWriters.isEmpty()) {
            messages.add(this.roomMessageWriters.poll());
        }
        if (updatedPlayers.size() > 0) {
            messages.add(new RoomPlayerStatusListWriter(updatedPlayers));
        }
        while (!this.playersToRemove.isEmpty()) {
            final RoomPlayer player = this.playersToRemove.poll();
            this.room.getRoomPlayers().remove(player.getVirtualId());
            this.room.onPlayerLeaves(player);
        }
        if (messages.size() > 0) {
            for (final RoomPlayer player : this.room.getRoomPlayers().values()) {
                if (player.isBot()) {
                    continue;
                }
                for (final MessageWriter message : messages) {
                    if (message.getSenderId() < 1 || player.getSession().getId() != message.getSenderId()) {
                        player.getSession().writeMessage(message);
                    }
                }
            }
        }
        while (!this.playersToAdd.isEmpty()) {
            final RoomPlayer player = this.playersToAdd.poll();
            this.room.getRoomPlayers().put(player.getVirtualId(), player);
            this.room.onPlayerEnters(player);
        }
        room.getWiredHandler().onTriggerPeriodically();
        final GapList<WiredDelayEvent> reAdd = new GapList<WiredDelayEvent>();
        while (!this.wiredItemDelay.isEmpty()) {
            final WiredDelayEvent delayEvent = this.wiredItemDelay.poll();
            delayEvent.getAction().onHandle(delayEvent.getPlayer(), delayEvent);
            if (!delayEvent.isFinished()) {
                reAdd.add(delayEvent);
            }
        }
        for (final WiredDelayEvent delay : reAdd) {
            this.wiredItemDelay.offer(delay);
        }
        if (this.updates == 0 || this.updates == 3) {
            Bootloader.getGame().getNavigatorListManager().setRoom(room.getInformation(), room.getInformation().getTotalPlayers());
            this.updates = 0;
        }
        this.updates++;
    }

}