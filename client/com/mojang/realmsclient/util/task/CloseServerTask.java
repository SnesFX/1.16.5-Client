/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsConfigureWorldScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.Logger;

public class CloseServerTask
extends LongRunningTask {
    private final RealmsServer serverData;
    private final RealmsConfigureWorldScreen configureScreen;

    public CloseServerTask(RealmsServer realmsServer, RealmsConfigureWorldScreen realmsConfigureWorldScreen) {
        this.serverData = realmsServer;
        this.configureScreen = realmsConfigureWorldScreen;
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableComponent("mco.configure.world.closing"));
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            if (this.aborted()) {
                return;
            }
            try {
                boolean bl = realmsClient.close(this.serverData.id);
                if (!bl) continue;
                this.configureScreen.stateChanged();
                this.serverData.state = RealmsServer.State.CLOSED;
                CloseServerTask.setScreen(this.configureScreen);
                break;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                CloseServerTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Failed to close server", (Throwable)exception);
                this.error("Failed to close the server");
            }
        }
    }
}

