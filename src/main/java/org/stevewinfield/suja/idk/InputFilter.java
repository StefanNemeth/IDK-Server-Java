/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk;

public class InputFilter {

    public static String filterString(final String x, final boolean removeLinebreaks) {
        String f = x.replace((char) 1, ' ').replace((char) 2, ' ').replace((char) 3, ' ').replace((char) 9, ' ');
        if (removeLinebreaks)
            f = f.replace((char) 13, ' ').replace((char) 10, ' ');
        return f;
    }

    public static String filterString(final String x) {
        return filterString(x, false);
    }

}