/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.trading;

import java.util.concurrent.ConcurrentHashMap;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.inventory.writers.UpdatePlayerInventoryWriter;
import org.stevewinfield.suja.idk.game.inventory.PlayerItem;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class Trade {
    public int getPlayerOne() {
        return playerOne;
    }

    public int getPlayerTwo() {
        return playerTwo;
    }

    public ConcurrentHashMap<Integer, PlayerItem> getOffersPlayerOne() {
        return offersPlayerOne;
    }

    public ConcurrentHashMap<Integer, PlayerItem> getOffersPlayerTwo() {
        return offersPlayerTwo;
    }

    public int getStage() {
        return stage;
    }

    public Trade(final int a, final int b) {
        this.playerOne = a;
        this.playerTwo = b;
        this.offersPlayerOne = new ConcurrentHashMap<Integer, PlayerItem>();
        this.offersPlayerTwo = new ConcurrentHashMap<Integer, PlayerItem>();
    }

    public boolean offerItem(final int playerId, final PlayerItem item) {
        if (stage != TradeStage.NEGOTIATING
        || ((playerId == playerOne && playerOneAccepted) || (playerId == playerTwo && playerTwoAccepted)))
            return false;

        if (item.getBase().isInventoryStackable()
        && ((playerId == playerOne && playerOneStackCount >= 9) || (playerId == playerTwo && playerTwoStackCount >= 9)))
            return false;

        if (offersPlayerOne.containsKey(item.getItemId()) || offersPlayerTwo.containsKey(item.getItemId()))
            return false;

        boolean addStack = true;

        if (playerId == playerOne) {
            for (final PlayerItem target : offersPlayerOne.values()) {
                if (target.getBase().getId() == target.getBase().getId() && target.getBase().isInventoryStackable()) {
                    addStack = false;
                    break;
                }
            }
            offersPlayerOne.put(item.getItemId(), item);
            if (addStack)
                ++playerOneStackCount;
        } else {
            for (final PlayerItem target : offersPlayerTwo.values()) {
                if (target.getBase().getId() == target.getBase().getId() && target.getBase().isInventoryStackable()) {
                    addStack = false;
                    break;
                }
            }
            offersPlayerTwo.put(item.getItemId(), item);
            if (addStack)
                ++playerTwoStackCount;
        }

        playerOneAccepted = false;
        playerTwoAccepted = false;
        return true;
    }

    public boolean modifyTrade(final int playerId) {
        if (this.stage != TradeStage.NEGOTIATING)
            return false;

        if (playerId == playerOne) {
            playerOneAccepted = false;
        } else {
            playerTwoAccepted = false;
        }
        return true;
    }

    public void deliverItems(final Session playerOne, final Session playerTwo) {
        final MessageWriter updateWriter = new UpdatePlayerInventoryWriter();
        for (final PlayerItem item : this.offersPlayerOne.values()) {
            playerOne.getPlayerInstance().getInventory().removeItem(item.getItemId(), null, false);
            playerTwo.getPlayerInstance().getInventory().addItem(item, true);
        }
        for (final PlayerItem item : this.offersPlayerTwo.values()) {
            playerTwo.getPlayerInstance().getInventory().removeItem(item.getItemId(), null, false);
            playerOne.getPlayerInstance().getInventory().addItem(item, true);
        }
        playerOne.writeMessage(updateWriter);
        playerTwo.writeMessage(updateWriter);
    }

    public boolean acceptTrade(final int playerId) {
        if (stage == TradeStage.FINALIZED)
            return false;

        if (playerId == playerOne)
            playerOneAccepted = true;
        else
            playerTwoAccepted = true;

        if (playerOneAccepted && playerTwoAccepted) {
            stage = stage == TradeStage.FINALIZING ? TradeStage.FINALIZED : TradeStage.FINALIZING;
            playerOneAccepted = false;
            playerTwoAccepted = false;
        }
        return true;
    }

    public boolean takeBackItem(final int playerId, final int itemId) {
        if (stage != TradeStage.NEGOTIATING
        || ((playerId == playerOne && playerOneAccepted) || (playerId == playerTwo && playerTwoAccepted)))
            return false;

        if (playerId == playerOne) {
            if (!this.offersPlayerOne.containsKey(itemId))
                return false;
            this.offersPlayerOne.remove(itemId);
            --playerOneStackCount;
        } else {
            if (!this.offersPlayerTwo.containsKey(itemId))
                return false;
            this.offersPlayerTwo.remove(itemId);
            --playerTwoStackCount;
        }

        playerOneAccepted = false;
        playerTwoAccepted = false;
        return true;
    }

    private final int playerOne;
    private final int playerTwo;
    private int stage;

    private boolean playerOneAccepted;
    private boolean playerTwoAccepted;

    private int playerOneStackCount;
    private int playerTwoStackCount;

    private final ConcurrentHashMap<Integer, PlayerItem> offersPlayerOne;
    private final ConcurrentHashMap<Integer, PlayerItem> offersPlayerTwo;
}
