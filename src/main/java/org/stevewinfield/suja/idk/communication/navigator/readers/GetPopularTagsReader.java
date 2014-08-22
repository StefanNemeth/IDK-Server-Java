/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.communication.navigator.readers;

import java.util.List;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;

import org.stevewinfield.suja.idk.Bootloader;
import org.stevewinfield.suja.idk.collections.PopularTagsListHelper;
import org.stevewinfield.suja.idk.communication.IMessageReader;
import org.stevewinfield.suja.idk.communication.MessageReader;
import org.stevewinfield.suja.idk.communication.navigator.writers.NavigatorPopularTagListWriter;
import org.stevewinfield.suja.idk.game.rooms.RoomInformation;
import org.stevewinfield.suja.idk.network.sessions.Session;

public class GetPopularTagsReader implements IMessageReader {

    @Override
    public void parse(final Session session, final MessageReader reader) {
        if (!session.isAuthenticated())
            return;

        final List<RoomInformation> topRooms = Bootloader.getGame().getNavigatorListManager().getNavigatorLists().get(-1)
        .getRooms();
        final ConcurrentHashMap<String, Integer> tags = new ConcurrentHashMap<String, Integer>();

        for (final RoomInformation roomInfo : topRooms) {
            if (roomInfo == null)
                continue;

            for (String tag : roomInfo.getSearchableTags()) {
                tag = tag.toLowerCase();
                if (!tag.isEmpty() && tag.length() <= 30) {
                    if (tags.containsKey(tag))
                        tags.put(tag, tags.get(tag) + 1);
                    else
                        tags.put(tag, 1);
                }
            }
        }

        final TreeMap<String, Integer> sortedTags = new TreeMap<String, Integer>(new PopularTagsListHelper(tags));
        sortedTags.putAll(tags);

        session.writeMessage(new NavigatorPopularTagListWriter(sortedTags));
    }

}
