/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.furnitures;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.concurrent.ConcurrentHashMap;

import org.apache.log4j.Logger;
import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.furnitures.interactors.*;
import org.stevewinfield.suja.idk.game.furnitures.interactors.games.banzai.*;
import org.stevewinfield.suja.idk.game.furnitures.interactors.wired.*;

public class FurnitureManager {
    private static Logger logger = Logger.getLogger(FurnitureManager.class);

    public Furniture getFurniture(final int furniId) {
        return this.furnitures.containsKey(furniId) ? furnitures.get(furniId) : null;
    }

    public ConcurrentHashMap<Integer, Furniture> getFurnitures() {
        return this.furnitures;
    }

    public ConcurrentHashMap<Integer, FurnitureExchange> getFurnitureExchanges() {
        return this.furnitureExchanges;
    }

    public IFurnitureInteractor getInteractor(final int interactorId) {
        return this.interactors.containsKey(interactorId) ? interactors.get(interactorId) : interactors.get(0);
    }

    public FurnitureManager() {
        this.loadCache();
        this.interactors = new ConcurrentHashMap<Integer, IFurnitureInteractor>();
        this.putInteractors();
        logger.info(this.interactors.size() + " Furni-Interactor(s) loaded.");
    }

    public void loadCache() {
        this.furnitures = new ConcurrentHashMap<Integer, Furniture>();
        this.furnitureExchanges = new ConcurrentHashMap<Integer, FurnitureExchange>();
        try {
            ResultSet row = Bootloader.getStorage().queryParams("SELECT * FROM furnitures").executeQuery();
            while (row.next()) {
                final Furniture furni = new Furniture();
                furni.set(row);
                this.furnitures.put(row.getInt("id"), furni);
            }
            row = Bootloader.getStorage().queryParams("SELECT * FROM furniture_exchanges").executeQuery();
            while (row.next()) {
                final FurnitureExchange furniExchange = new FurnitureExchange();
                furniExchange.set(row);
                this.furnitureExchanges.put(row.getInt("id"), furniExchange);
            }
            row.close();
            logger.info(this.furnitures.size() + " Furniture(s) loaded.");
            logger.info(this.furnitureExchanges.size() + " Furniture Exchange(s) loaded.");
        } catch (final SQLException e) {
            logger.error("SQL Exception", e);
        }
    }

    private void putInteractors() {
        this.interactors.put(FurnitureInteractor.DEFAULT, new DefaultInteractor());
        this.interactors.put(FurnitureInteractor.DEFAULT_SWITCH, new DefaultSwitchInteractor());
        this.interactors.put(FurnitureInteractor.GATE, new GateInteractor());
        this.interactors.put(FurnitureInteractor.VENDING, new VendingInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_SAYS, new WiredTriggerUserSaysInteractor());
        this.interactors.put(FurnitureInteractor.WF_ACT_SHOW_MESSAGE, new WiredActionShowMessageInteractor());
        this.interactors.put(FurnitureInteractor.WF_ACT_TOGGLE_FURNI, new WiredActionToggleFurniInteractor());
        this.interactors.put(FurnitureInteractor.FIREWORK, new FireworkInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_ENTER_ROOM, new WiredTriggerEnterRoomInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_WALKS_ON_FURNI, new WiredTriggerUserWalksOnFurniInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_WALKS_OFF_FURNI, new WiredTriggerUserWalksOffFurniInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_TIMER, new BattleBanzaiTimerInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_PATCH, new BattleBanzaiPatchInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_GATE_PINK, new BattleBanzaiPinkGateInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_GATE_GREEN, new BattleBanzaiGreenGateInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_GATE_BLUE, new BattleBanzaiBlueGateInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_GATE_ORANGE, new BattleBanzaiOrangeGateInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_SCOREBOARD_PINK,
        new BattleBanzaiPinkScoreboardInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_SCOREBOARD_GREEN,
        new BattleBanzaiGreenScoreboardInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_SCOREBOARD_BLUE,
        new BattleBanzaiBlueScoreboardInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_SCOREBOARD_ORANGE,
        new BattleBanzaiOrangeScoreboardInteractor());
        this.interactors.put(FurnitureInteractor.EXCHANGE, new ExchangeInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_STATE_CHANGED, new WiredTriggerStateChangedInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_PERIODICALLY, new WiredTriggerPeriodicallyInteractor());
        this.interactors.put(FurnitureInteractor.WF_SWITCH, new WiredSwitchInteractor());
        this.interactors.put(FurnitureInteractor.WF_PLATE, new WiredPlateInteractor());
        this.interactors.put(FurnitureInteractor.BATTLE_BANZAI_PUCK, new BattleBanzaiPuckInteractor());
        this.interactors.put(FurnitureInteractor.RANDOM, new RandomInteractor());
        this.interactors.put(FurnitureInteractor.GIFT, new DefaultInteractor());
        this.interactors.put(FurnitureInteractor.STICKIE_POLE, new StickiePoleInteractor());
        this.interactors.put(FurnitureInteractor.TELEPORTER, new TeleporterInteractor());
        this.interactors.put(FurnitureInteractor.ROLLER, new RollerInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_GAME_ENDS, new WiredTriggerGameEndsInteractor());
        this.interactors.put(FurnitureInteractor.WF_TRG_GAME_STARTS, new WiredTriggerGameStartsInteractor());
    }

    private ConcurrentHashMap<Integer, Furniture> furnitures;
    private ConcurrentHashMap<Integer, FurnitureExchange> furnitureExchanges;
    private final ConcurrentHashMap<Integer, IFurnitureInteractor> interactors;
}
