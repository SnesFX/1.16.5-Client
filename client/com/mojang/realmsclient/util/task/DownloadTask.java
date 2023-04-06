/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.WorldDownload;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsDownloadLatestWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.util.task.LongRunningTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.Logger;

public class DownloadTask
extends LongRunningTask {
    private final long worldId;
    private final int slot;
    private final Screen lastScreen;
    private final String downloadName;

    public DownloadTask(long l, int n, String string, Screen screen) {
        this.worldId = l;
        this.slot = n;
        this.lastScreen = screen;
        this.downloadName = string;
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableComponent("mco.download.preparing"));
        RealmsClient realmsClient = RealmsClient.create();
        for (int i = 0; i < 25; ++i) {
            try {
                if (this.aborted()) {
                    return;
                }
                WorldDownload worldDownload = realmsClient.requestDownloadInfo(this.worldId, this.slot);
                DownloadTask.pause(1);
                if (this.aborted()) {
                    return;
                }
                DownloadTask.setScreen(new RealmsDownloadLatestWorldScreen(this.lastScreen, worldDownload, this.downloadName, bl -> {}));
                return;
            }
            catch (RetryCallException retryCallException) {
                if (this.aborted()) {
                    return;
                }
                DownloadTask.pause(retryCallException.delaySeconds);
                continue;
            }
            catch (RealmsServiceException realmsServiceException) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data");
                DownloadTask.setScreen(new RealmsGenericErrorScreen(realmsServiceException, this.lastScreen));
                return;
            }
            catch (Exception exception) {
                if (this.aborted()) {
                    return;
                }
                LOGGER.error("Couldn't download world data", (Throwable)exception);
                this.error(exception.getLocalizedMessage());
                return;
            }
        }
    }
}

