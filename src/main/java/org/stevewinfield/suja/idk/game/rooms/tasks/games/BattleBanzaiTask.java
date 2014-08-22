/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.game.rooms.tasks.games;

import java.util.LinkedList;
import java.util.List;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.game.rooms.GameTeam;
import org.stevewinfield.suja.idk.game.rooms.RoomInstance;
import org.stevewinfield.suja.idk.game.rooms.RoomItem;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayer;
import org.stevewinfield.suja.idk.game.rooms.RoomPlayerEffect;
import org.stevewinfield.suja.idk.game.rooms.tasks.GameTask;

public class BattleBanzaiTask extends GameTask {
    public BattleBanzaiTask(final RoomInstance room) {
        super(room);
    }

    @Override
    public void onGameEnds(final RoomItem timer) {
        this.running = false;
        this.paused = false;
        this.ended = true;
        GameTeam winner = null;
        int highScore = 0;
        for (final GameTeam team : this.gameTeams.values()) {
            if (team.getGate() != null)
                team.getGate().updateState(true, true);
            if (team.getPoints() > 0 && team.getPoints() == highScore) {
                winner = null;
            } else if (team.getPoints() > highScore) {
                highScore = team.getPoints();
                winner = team;
            }
        }
        if (winner != null) {
            for (final RoomPlayer player : winner.getPlayers().values()) {
                player.wave();
            }
        }
        for (final RoomItem banzaiPatch : this.gameItems) {
            if (banzaiPatch.getFlagsState() == 1) {
                banzaiPatch.setFlags(0);
                banzaiPatch.update(false, true);
                continue;
            }

            if (winner == null)
                continue;

            final int state = banzaiPatch.getFlagsState();

            if ((winner.getId() == RoomPlayerEffect.BANZAI_PINK && state == 5)
            || (winner.getId() == RoomPlayerEffect.BANZAI_GREEN && state == 8)
            || (winner.getId() == RoomPlayerEffect.BANZAI_BLUE && state == 11)
            || (winner.getId() == RoomPlayerEffect.BANZAI_ORANGE && state == 14)) {
                this.flexInteger = state;
                banzaiPatch.setFlags(0);
                banzaiPatch.update(false, true);
                banzaiPatch.setCounter(0);
                banzaiPatch.requestCycles(1);
            }
        }
        super.onGameEnds(timer);
    }

    @Override
    public void onGameRests(final RoomItem timer) {
        this.running = false;
        this.paused = true;
        for (final GameTeam team : this.gameTeams.values()) {
            if (team.getGate() != null)
                team.getGate().updateState(true, false);
        }
    }

    @Override
    public void onGameStarts(final RoomItem timer) {
        for (final GameTeam team : this.gameTeams.values()) {
            if (team.getGate() != null)
                team.getGate().updateState(false, false);
        }
        if (this.ended) {
            for (final RoomItem banzaiPatch : this.gameItems) {
                banzaiPatch.setFlags(1);
                banzaiPatch.update(false, true);
            }
            for (final GameTeam team : this.gameTeams.values()) {
                team.resetScore();
            }
        }
        this.running = true;
        this.paused = false;
        this.ended = false;
        super.onGameStarts(timer);
    }

    public static boolean isHighlighted(final RoomItem item) {
        return item.getFlagsState() == 5 || item.getFlagsState() == 8 || item.getFlagsState() == 11
        || item.getFlagsState() == 14;
    }

    public static List<RoomItem> buildBanzaiRectangle(final RoomItem triggerItem, final int x, final int y, final int goX,
    final int goY, final int currentDirection, final int turns, final int teamId) {
        final boolean[] directions = new boolean[4];

        if (goX == -1 || goX == 0) {
            directions[0] = true;
        }
        if (goX == 1 || goX == 0) {
            directions[2] = true;
        }
        if (goY == -1 || goY == 0) {
            directions[1] = true;
        }
        if (goY == 1 || goY == 0) {
            directions[3] = true;
        }

        if ((goX != 0 || goY != 0) && triggerItem.getPosition().getX() == x && triggerItem.getPosition().getY() == y) {
            return new LinkedList<RoomItem>();
        }

        final RoomInstance room = triggerItem.getRoom();

        for (int i = 0; i < 4; ++i) {
            if (!directions[i]) {
                continue;
            }

            int nextXStep = 0, nextYStep = 0;

            if (i == 0 || i == 2) {
                nextXStep = (i == 0) ? 1 : -1;
            } else if (i == 1 || i == 3) {
                nextYStep = (i == 1) ? 1 : -1;
            }

            final int nextX = x + nextXStep;
            final int nextY = y + nextYStep;

            final RoomItem item = room.getTopItem(nextX, nextY);

            if (item != null && item.getFlagsState() == teamId) {
                List<RoomItem> foundPatches = null;
                if (currentDirection != i && currentDirection != -1) {
                    if (turns > 0) {
                        foundPatches = BattleBanzaiTask.buildBanzaiRectangle(triggerItem, nextX, nextY,
                        (nextXStep == 0) ? (goX * -1) : (nextXStep * -1), (nextYStep == 0) ? (goY * -1)
                        : (nextYStep * -1), i, (turns - 1), teamId);
                    }
                } else {
                    foundPatches = BattleBanzaiTask.buildBanzaiRectangle(triggerItem, nextX, nextY,
                    (nextXStep == 0) ? goX : (nextXStep * -1), (nextYStep == 0) ? goY : (nextYStep * -1), i, turns,
                    teamId);
                }
                if (foundPatches != null) {
                    foundPatches.add(item);
                    return foundPatches;
                }
            }
        }
        return null;
    }

    @Override
    public void onHandle(final RoomPlayer player, final RoomItem item) {
        final GameTeam team = this.getTeam(player.getEffectId());

        if (team == null)
            return;

        int state = item.getFlagsState();
        boolean givePoint = false;

        if (BattleBanzaiTask.isHighlighted(item))
            return;

        if ((state < 5 && player.getEffectId() != RoomPlayerEffect.BANZAI_PINK)
        || (state < 8 && state > 5 && player.getEffectId() != RoomPlayerEffect.BANZAI_GREEN)
        || (state < 11 && state > 8 && player.getEffectId() != RoomPlayerEffect.BANZAI_BLUE)
        || (state < 13 && state > 11 && player.getEffectId() != RoomPlayerEffect.BANZAI_ORANGE)) {
            state = 0;
        }

        switch (player.getEffectId()) {
        case RoomPlayerEffect.BANZAI_PINK:
            if (state < 3)
                state = 2;

            if (state - 2 >= 2) {
                givePoint = true;
            }
            break;
        case RoomPlayerEffect.BANZAI_GREEN:
            if (state < 6)
                state = 5;

            if (state - 5 >= 2) {
                givePoint = true;
            }
            break;
        case RoomPlayerEffect.BANZAI_BLUE:
            if (state < 9)
                state = 8;

            if (state - 8 >= 2) {
                givePoint = true;
            }
            break;
        case RoomPlayerEffect.BANZAI_ORANGE:
            if (state < 12)
                state = 11;

            if (state - 11 >= 2) {
                givePoint = true;
            }
            break;
        }

        item.setFlags(++state);
        item.update(false, true);

        if (givePoint) {
            int points = 1;
            int empty = 0;

            try {
                final List<RoomItem> tilesToAdd = BattleBanzaiTask.buildBanzaiRectangle(item, item.getPosition().getX(), item
                .getPosition().getY(), 0, 0, -1, 4, state);
                if (tilesToAdd != null) {
                    for (final RoomItem _item : this.gameItems) {
                        if (BattleBanzaiTask.isHighlighted(_item)) {
                            continue;
                        }
                        final boolean[] borderCheck = new boolean[4];
                        for (final RoomItem borderItem : tilesToAdd) {
                            if (borderItem.getPosition().getY() == _item.getPosition().getY()) {
                                if (borderItem.getPosition().getX() > _item.getPosition().getX()) {
                                    borderCheck[0] = true;
                                } else {
                                    borderCheck[1] = true;
                                }
                            } else if (borderItem.getPosition().getX() == _item.getPosition().getX()) {
                                if (borderItem.getPosition().getY() > _item.getPosition().getY()) {
                                    borderCheck[2] = true;
                                } else {
                                    borderCheck[3] = true;
                                }
                            }
                        }
                        if (borderCheck[0] && borderCheck[1] && borderCheck[2] && borderCheck[3]) {
                            _item.setFlags(state);
                            _item.update(false, true);
                            ++points;
                        }
                    }
                }
                for (final RoomItem fillItem : this.gameItems) {
                    if (!BattleBanzaiTask.isHighlighted(fillItem)) {
                        empty++;
                    }
                }
            } catch (final Exception e) {
                e.printStackTrace();
            }

            if (!player.isBot() && player.getSession() != null) {
                Bootloader.getGame().getAchievementManager()
                .progressAchievement(player.getSession(), "ACH_BattleBallTilesLocked", points);
                team.onPointReceived(points);
            }
            if (empty < 1) {
                this.running = false;
            }
        }
    }
}
