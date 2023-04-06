/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package com.mojang.realmsclient.client;

import com.google.common.collect.Lists;
import com.mojang.realmsclient.dto.RegionPingResult;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import net.minecraft.Util;

public class Ping {
    public static List<RegionPingResult> ping(Region ... arrregion) {
        for (Region region : arrregion) {
            Ping.ping(region.endpoint);
        }
        ArrayList arrayList = Lists.newArrayList();
        for (Region region : arrregion) {
            arrayList.add(new RegionPingResult(region.name, Ping.ping(region.endpoint)));
        }
        arrayList.sort(Comparator.comparingInt(RegionPingResult::ping));
        return arrayList;
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    private static int ping(String string) {
        int n = 700;
        long l = 0L;
        Socket socket = null;
        for (int i = 0; i < 5; ++i) {
            try {
                InetSocketAddress inetSocketAddress = new InetSocketAddress(string, 80);
                socket = new Socket();
                long l2 = Ping.now();
                socket.connect(inetSocketAddress, 700);
                l += Ping.now() - l2;
                Ping.close(socket);
                continue;
            }
            catch (Exception exception) {
                l += 700L;
                continue;
            }
            finally {
                Ping.close(socket);
            }
        }
        return (int)((double)l / 5.0);
    }

    private static void close(Socket socket) {
        try {
            if (socket != null) {
                socket.close();
            }
        }
        catch (Throwable throwable) {
            // empty catch block
        }
    }

    private static long now() {
        return Util.getMillis();
    }

    public static List<RegionPingResult> pingAllRegions() {
        return Ping.ping(Region.values());
    }

    static enum Region {
        US_EAST_1("us-east-1", "ec2.us-east-1.amazonaws.com"),
        US_WEST_2("us-west-2", "ec2.us-west-2.amazonaws.com"),
        US_WEST_1("us-west-1", "ec2.us-west-1.amazonaws.com"),
        EU_WEST_1("eu-west-1", "ec2.eu-west-1.amazonaws.com"),
        AP_SOUTHEAST_1("ap-southeast-1", "ec2.ap-southeast-1.amazonaws.com"),
        AP_SOUTHEAST_2("ap-southeast-2", "ec2.ap-southeast-2.amazonaws.com"),
        AP_NORTHEAST_1("ap-northeast-1", "ec2.ap-northeast-1.amazonaws.com"),
        SA_EAST_1("sa-east-1", "ec2.sa-east-1.amazonaws.com");
        
        private final String name;
        private final String endpoint;

        private Region(String string2, String string3) {
            this.name = string2;
            this.endpoint = string3;
        }
    }

}

