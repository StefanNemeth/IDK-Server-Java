/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;

public class PlayerObjectWriter extends MessageWriter {

    public PlayerObjectWriter(final PlayerInformation playerInfo) {
        super(OperationCodes.getOutgoingOpCode("PlayerObject"));
        super.push(playerInfo.getId() + "");
        super.push(playerInfo.getPlayerName());
        super.push(playerInfo.getAvatar());
        super.push(playerInfo.getGender() == PlayerInformation.FEMALE_GENDER ? "F" : "M");
        super.push(playerInfo.getMission());
        super.push(playerInfo.getPlayerName());
        super.push(false);
        super.push(playerInfo.getRespectPoints());
        super.push(playerInfo.getAvailableRespects());
        super.push(playerInfo.getAvailableScratches());
        super.push(playerInfo.isStreamEnabled());
    }

}
