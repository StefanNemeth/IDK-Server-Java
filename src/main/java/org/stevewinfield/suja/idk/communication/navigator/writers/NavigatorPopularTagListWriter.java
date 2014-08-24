/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.writers;

import org.stevewinfield.suja.idk.communication.MessageWriter;
import org.stevewinfield.suja.idk.communication.OperationCodes;

import java.util.Map.Entry;
import java.util.TreeMap;

public class NavigatorPopularTagListWriter extends MessageWriter {

    public NavigatorPopularTagListWriter(final TreeMap<String, Integer> popularTags) {
        super(OperationCodes.getOutgoingOpCode("NavigatorPopularTagList"));
        super.push(popularTags.size());

        for (final Entry<String, Integer> entry : popularTags.entrySet()) {
            super.push(entry.getKey());
            super.push(entry.getValue());
        }
    }

}
