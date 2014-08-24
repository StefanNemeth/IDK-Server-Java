/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.collections;

import org.stevewinfield.suja.idk.game.rooms.RoomInformation;

import java.util.Comparator;

public class NavigatorListHelper implements Comparator<RoomInformation> {
    String searchQuery;

    public NavigatorListHelper() {
    }

    public NavigatorListHelper(final String searchQuery) {
        this.searchQuery = searchQuery;
    }

    @Override
    public int compare(final RoomInformation o1, final RoomInformation o2) {
        if (searchQuery != null && o1.getTotalPlayers() == o2.getTotalPlayers()) {
            int points1 = 0;
            int points2 = 0;
            for (int i = 0; i < this.searchQuery.length(); i++) {
                if (o1.getName().length() >= (i + 1) && o1.getName().charAt(i) == this.searchQuery.charAt(i)) {
                    points1++;
                }
                if (o2.getName().length() >= (i + 1) && o2.getName().charAt(i) == this.searchQuery.charAt(i)) {
                    points2++;
                }
            }
            return ((Integer) points1).compareTo(points2) * (-1);
        }
        return ((Integer) o1.getTotalPlayers()).compareTo(o2.getTotalPlayers()) * (-1);
    }

}