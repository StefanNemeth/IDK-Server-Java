/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.room.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;
import org.stevewinfield.suja.idk.game.miscellaneous.MoodlightData;
import org.stevewinfield.suja.idk.game.miscellaneous.MoodlightPreset;

import java.util.Map.Entry;

public class MoodlightDataWriter extends MessageWriter {

    public MoodlightDataWriter(final MoodlightData data) {
        super(OperationCodes.getOutgoingOpCode("MoodlightData"));
        super.push(data.getPresets().size());
        super.push(data.getCurrentPreset());

        for (final Entry<Integer, MoodlightPreset> preset : data.getPresets().entrySet()) {
            super.push(preset.getKey());
            super.push(preset.getValue().isOnlyBackground() ? 2 : 1);
            super.push(preset.getValue().getColorCode());
            super.push(preset.getValue().getColorIntensity());
        }
    }

}
