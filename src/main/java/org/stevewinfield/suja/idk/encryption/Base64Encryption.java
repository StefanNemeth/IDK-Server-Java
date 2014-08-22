/*
 * IDK Game Server by Steve Winfield
 * https://github.com/WinfieldSteve
 */
package org.stevewinfield.suja.idk.encryption;

public class Base64Encryption {
    public static byte[] encode(final int i) {
        final byte[] data = new byte[2];
        for (int j = 1; j <= 2; j++) {
            data[j - 1] = (byte) (64 + ((i >> (2 - j) * 6) & 63));
        }
        return data;
    }

    public static int decode(final String s) {
        try {
            final char[] val = s.toCharArray();
            int intTot = 0;
            int y = 0;
            for (int x = (val.length - 1); x >= 0; x--) {
                int intTmp = (byte) ((val[x] - 64));
                if (y > 0)
                    intTmp = intTmp * (int) (Math.pow(64, y));
                intTot += intTmp;
                y++;
            }
            return intTot;
        } catch (final Exception e) {
            System.out.println(e.toString());
            return 0;
        }
    }
}
