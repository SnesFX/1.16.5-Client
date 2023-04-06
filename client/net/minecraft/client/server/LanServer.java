/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.client.server;

import net.minecraft.Util;

public class LanServer {
    private final String motd;
    private final String address;
    private long pingTime;

    public LanServer(String string, String string2) {
        this.motd = string;
        this.address = string2;
        this.pingTime = Util.getMillis();
    }

    public String getMotd() {
        return this.motd;
    }

    public String getAddress() {
        return this.address;
    }

    public void updatePingTime() {
        this.pingTime = Util.getMillis();
    }
}

