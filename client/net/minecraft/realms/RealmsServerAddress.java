/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.realms;

import net.minecraft.client.multiplayer.ServerAddress;

public class RealmsServerAddress {
    private final String host;
    private final int port;

    protected RealmsServerAddress(String string, int n) {
        this.host = string;
        this.port = n;
    }

    public String getHost() {
        return this.host;
    }

    public int getPort() {
        return this.port;
    }

    public static RealmsServerAddress parseString(String string) {
        ServerAddress serverAddress = ServerAddress.parseString(string);
        return new RealmsServerAddress(serverAddress.getHost(), serverAddress.getPort());
    }
}

