/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.player.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.players.PlayerInformation;

public class PlayerInfoUpdateWriter extends MessageWriter {

    public PlayerInfoUpdateWriter(final int actorId, final String avatar, final int gender, final String motto,
    final int score) {
        super(OperationCodes.getOutgoingOpCode("PlayerInfoUpdate"));
        super.push(actorId);
        super.push(avatar);
        super.push(gender == PlayerInformation.FEMALE_GENDER ? "f" : "m");
        super.push(motto);
        super.push(score);
    }

}
