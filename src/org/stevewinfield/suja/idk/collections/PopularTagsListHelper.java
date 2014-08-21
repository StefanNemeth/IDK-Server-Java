/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.collections;

import java.util.Comparator;
import java.util.Map;

public class PopularTagsListHelper implements Comparator<String> {
    Map<String, Integer> base;

    public PopularTagsListHelper(final Map<String, Integer> base) {
        this.base = base;
    }

    @Override
    public int compare(final String o1, final String o2) {
        final int compare = base.get(o1).compareTo(base.get(o2)) * (-1);
        if (compare == 0) {
            return -1;
        }
        return compare;
    }

}
