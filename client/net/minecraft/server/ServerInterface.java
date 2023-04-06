/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.server;

import net.minecraft.server.dedicated.DedicatedServerProperties;

public interface ServerInterface {
    public DedicatedServerProperties getProperties();

    public String getServerIp();

    public int getServerPort();

    public String getServerName();

    public String getServerVersion();

    public int getPlayerCount();

    public int getMaxPlayers();

    public String[] getPlayerNames();

    public String getLevelIdName();

    public String getPluginNames();

    public String runCommand(String var1);
}

