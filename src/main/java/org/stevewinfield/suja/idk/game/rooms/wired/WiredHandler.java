/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.wired;

import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.coordination.Vector2;
import org.stevewinfield.suja.idk.game.rooms.wired.triggers.*;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class WiredHandler {
    public ConcurrentHashMap<Vector2, ConcurrentHashMap<Integer, IWiredItem>> getStack() {
        return stack;
    }

    public WiredHandler(final RoomInstance room) {
        this.stack = new ConcurrentHashMap<>();
    }

    public void addItem(final Vector2 position, final IWiredItem item) {
        if (!this.stack.containsKey(position)) {
            this.stack.put(position, new ConcurrentHashMap<Integer, IWiredItem>());
        }
        this.stack.get(position).put(item.getItem().getItemId(), item);
    }

    public void itemTriggered(final WiredTrigger trigger, final RoomPlayer player, final Vector2 pos) {
        for (final IWiredItem item : this.stack.get(pos).values()) {
            if (item instanceof WiredAction) {
                final WiredAction action = (WiredAction) item;
                this.lightItem(action);
                if (player == null) {
                    for (final RoomPlayer sPlayer : trigger.getItem().getRoom().getRoomPlayers().values()) {
                        action.onHandle(sPlayer);
                    }
                } else {
                    action.onHandle(player);
                }
            }
        }
    }

    public void onFloorItemRemoved(final RoomItem item) {
        final RoomInstance room = item.getRoom();

        for (final RoomItem checkWired : room.getRoomItems().values()) {
            if (WiredManager.isWiredItem(checkWired.getBase()) &&
                    this.getStack().containsKey(checkWired.getPosition().getVector2()) &&
                    this.getStack().get(checkWired.getPosition().getVector2()).get(checkWired.getItemId()) != null) {
                final IWiredItem wiredItem = this.getStack().get(checkWired.getPosition().getVector2()).get(checkWired.getItemId());
                if (wiredItem != null && wiredItem.getItems() != null && wiredItem.getItems().contains(item.getItemId())) {
                    String furniString = "";
                    for (final int furni : wiredItem.getItems()) {
                        if (furni == item.getItemId()) {
                            continue;
                        }
                        furniString += "," + furni;
                    }
                    final String[] obj = wiredItem.getDelay() > -1 ? new String[]{furniString.length() > 0 ? furniString.substring(1) : furniString, wiredItem.getDelay() + ""} : new String[]{furniString.length() > 0 ? furniString.substring(1) : furniString};
                    wiredItem.set(obj);
                    checkWired.setTermFlags(obj);
                }
            }
        }
    }

    public void lightItem(final IWiredItem item) {
        item.getItem().setFlags(1);
        item.getItem().update(false);
        item.getItem().requestCycles(1);
    }

    public boolean onPlayerSays(final RoomPlayer player, final String message) {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerUserSays && ((WiredTrigger) item).onTrigger(player, message)) {
                    if (!x) {
                        player.whisper(player.getSession(), player.getVirtualId(), message);
                    }
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, player, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onPlayerEnters(final RoomPlayer player) {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerEnterRoom && ((WiredTrigger) item).onTrigger(player, null)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, player, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onPlayerWalksOnFurni(final RoomPlayer player, final RoomItem obj) {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerUserWalksOnFurni && ((WiredTrigger) item).onTrigger(player, obj)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, player, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onPlayerChangedState(final RoomPlayer player, final RoomItem obj) {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerStateChanged && ((WiredTrigger) item).onTrigger(player, obj)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, player, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onPlayerWalksOffFurni(final RoomPlayer player, final RoomItem obj) {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerUserWalksOffFurni && ((WiredTrigger) item).onTrigger(player, obj)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, player, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onTriggerPeriodically() {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerPeriodically && ((WiredTrigger) item).onTrigger(null, null)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, null, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onGameEnds() {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerGameEnds && ((WiredTrigger) item).onTrigger(null, null)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, null, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public boolean onGameStarts() {
        boolean x = false;
        for (final Map.Entry<Vector2, ConcurrentHashMap<Integer, IWiredItem>> e : stack.entrySet()) {
            boolean x2 = false;
            for (final IWiredItem item : e.getValue().values()) {
                if (item.getWiredType() == 1 && item instanceof WiredTriggerGameStarts && ((WiredTrigger) item).onTrigger(null, null)) {
                    this.lightItem(item);
                    if (!x2) {
                        this.itemTriggered((WiredTrigger) item, null, e.getKey());
                    }
                    x = true;
                    x2 = true;
                }
            }
        }
        return x;
    }

    public void saveWired(final RoomItem item, final MessageReader reader) {
        if (!this.stack.get(item.getPosition().getVector2()).containsKey(item.getItemId())) {
            return;
        }

        final IWiredItem wired = this.stack.get(item.getPosition().getVector2()).get(item.getItemId());

        final String[] obj = wired.getObject(reader);

        if (obj != null) {
            wired.set(obj);
            item.setTermFlags(obj);
        }

        item.update(true, false);
    }

    public void moveWired(final RoomItem item, final Vector2 oldCoords) {
        this.addItem(item.getPosition().getVector2(), this.stack.get(oldCoords).remove(item.getItemId()));
    }

    public void removeItem(final RoomItem item, final Vector2 coords) {
        this.stack.get(coords).remove(item.getItemId());
    }

    private final ConcurrentHashMap<Vector2, ConcurrentHashMap<Integer, IWiredItem>> stack;
}
