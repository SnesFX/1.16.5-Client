/*
 * Decompiled with CFR 0.146.
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.realms.RealmsConnect;

public class ConnectTask
extends LongRunningTask {
    private final RealmsConnect realmsConnect;
    private final RealmsServer server;
    private final RealmsServerAddress address;

    public ConnectTask(Screen screen, RealmsServer realmsServer, RealmsServerAddress realmsServerAddress) {
        this.server = realmsServer;
        this.address = realmsServerAddress;
        this.realmsConnect = new RealmsConnect(screen);
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableComponent("mco.connect.connecting"));
        net.minecraft.realms.RealmsServerAddress realmsServerAddress = net.minecraft.realms.RealmsServerAddress.parseString(this.address.address);
        this.realmsConnect.connect(this.server, realmsServerAddress.getHost(), realmsServerAddress.getPort());
    }

    @Override
    public void abortTask() {
        this.realmsConnect.abort();
        Minecraft.getInstance().getClientPackSource().clearServerPack();
    }

    @Override
    public void tick() {
        this.realmsConnect.tick();
    }
}

