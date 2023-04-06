/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server.rcon;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;

public class PktUtils {
    public static final char[] HEX_CHAR = new char[]{'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    public static String stringFromByteArray(byte[] arrby, int n, int n2) {
        int n3;
        int n4 = n2 - 1;
        int n5 = n3 = n > n4 ? n4 : n;
        while (0 != arrby[n3] && n3 < n4) {
            ++n3;
        }
        return new String(arrby, n, n3 - n, StandardCharsets.UTF_8);
    }

    public static int intFromByteArray(byte[] arrby, int n) {
        return PktUtils.intFromByteArray(arrby, n, arrby.length);
    }

    public static int intFromByteArray(byte[] arrby, int n, int n2) {
        if (0 > n2 - n - 4) {
            return 0;
        }
        return arrby[n + 3] << 24 | (arrby[n + 2] & 0xFF) << 16 | (arrby[n + 1] & 0xFF) << 8 | arrby[n] & 0xFF;
    }

    public static int intFromNetworkByteArray(byte[] arrby, int n, int n2) {
        if (0 > n2 - n - 4) {
            return 0;
        }
        return arrby[n] << 24 | (arrby[n + 1] & 0xFF) << 16 | (arrby[n + 2] & 0xFF) << 8 | arrby[n + 3] & 0xFF;
    }

    public static String toHexString(byte by) {
        return "" + HEX_CHAR[(by & 0xF0) >>> 4] + HEX_CHAR[by & 0xF];
    }
}

