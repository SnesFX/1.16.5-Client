/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  it.unimi.dsi.fastutil.booleans.BooleanConsumer
 *  org.apache.logging.log4j.Logger
 */
package com.mojang.realmsclient.util.task;

import com.mojang.realmsclient.RealmsMainScreen;
import com.mojang.realmsclient.client.RealmsClient;
import com.mojang.realmsclient.dto.RealmsServer;
import com.mojang.realmsclient.dto.RealmsServerAddress;
import com.mojang.realmsclient.exception.RealmsServiceException;
import com.mojang.realmsclient.exception.RetryCallException;
import com.mojang.realmsclient.gui.screens.RealmsBrokenWorldScreen;
import com.mojang.realmsclient.gui.screens.RealmsGenericErrorScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongConfirmationScreen;
import com.mojang.realmsclient.gui.screens.RealmsLongRunningMcoTaskScreen;
import com.mojang.realmsclient.gui.screens.RealmsTermsScreen;
import com.mojang.realmsclient.util.task.ConnectTask;
import com.mojang.realmsclient.util.task.LongRunningTask;
import it.unimi.dsi.fastutil.booleans.BooleanConsumer;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;
import java.util.concurrent.locks.ReentrantLock;
import java.util.function.Function;
import net.minecraft.client.Minecraft;
import net.minecraft.client.User;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.ClientPackSource;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import org.apache.logging.log4j.Logger;

public class GetServerDetailsTask
extends LongRunningTask {
    private final RealmsServer server;
    private final Screen lastScreen;
    private final RealmsMainScreen mainScreen;
    private final ReentrantLock connectLock;

    public GetServerDetailsTask(RealmsMainScreen realmsMainScreen, Screen screen, RealmsServer realmsServer, ReentrantLock reentrantLock) {
        this.lastScreen = screen;
        this.mainScreen = realmsMainScreen;
        this.server = realmsServer;
        this.connectLock = reentrantLock;
    }

    @Override
    public void run() {
        this.setTitle(new TranslatableComponent("mco.connect.connecting"));
        RealmsClient realmsClient = RealmsClient.create();
        boolean bl2 = false;
        boolean bl3 = false;
        int n = 5;
        RealmsServerAddress realmsServerAddress = null;
        boolean bl4 = false;
        boolean bl5 = false;
        for (int i = 0; i < 40 && !this.aborted(); ++i) {
            try {
                realmsServerAddress = realmsClient.join(this.server.id);
                bl2 = true;
            }
            catch (RetryCallException retryCallException) {
                n = retryCallException.delaySeconds;
            }
            catch (RealmsServiceException realmsServiceException) {
                if (realmsServiceException.errorCode == 6002) {
                    bl4 = true;
                    break;
                }
                if (realmsServiceException.errorCode == 6006) {
                    bl5 = true;
                    break;
                }
                bl3 = true;
                this.error(realmsServiceException.toString());
                LOGGER.error("Couldn't connect to world", (Throwable)realmsServiceException);
                break;
            }
            catch (Exception exception) {
                bl3 = true;
                LOGGER.error("Couldn't connect to world", (Throwable)exception);
                this.error(exception.getLocalizedMessage());
                break;
            }
            if (bl2) break;
            this.sleep(n);
        }
        if (bl4) {
            GetServerDetailsTask.setScreen(new RealmsTermsScreen(this.lastScreen, this.mainScreen, this.server));
        } else if (bl5) {
            if (this.server.ownerUUID.equals(Minecraft.getInstance().getUser().getUuid())) {
                GetServerDetailsTask.setScreen(new RealmsBrokenWorldScreen(this.lastScreen, this.mainScreen, this.server.id, this.server.worldType == RealmsServer.WorldType.MINIGAME));
            } else {
                GetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(new TranslatableComponent("mco.brokenworld.nonowner.title"), new TranslatableComponent("mco.brokenworld.nonowner.error"), this.lastScreen));
            }
        } else if (!this.aborted() && !bl3) {
            if (bl2) {
                RealmsServerAddress realmsServerAddress2 = realmsServerAddress;
                if (realmsServerAddress2.resourcePackUrl != null && realmsServerAddress2.resourcePackHash != null) {
                    TranslatableComponent translatableComponent = new TranslatableComponent("mco.configure.world.resourcepack.question.line1");
                    TranslatableComponent translatableComponent2 = new TranslatableComponent("mco.configure.world.resourcepack.question.line2");
                    GetServerDetailsTask.setScreen(new RealmsLongConfirmationScreen(bl -> {
                        try {
                            if (bl) {
                                Function<Throwable, Void> function = throwable -> {
                                    Minecraft.getInstance().getClientPackSource().clearServerPack();
                                    LOGGER.error(throwable);
                                    GetServerDetailsTask.setScreen(new RealmsGenericErrorScreen(new TextComponent("Failed to download resource pack!"), this.lastScreen));
                                    return null;
                                };
                                try {
                                    ((CompletableFuture)Minecraft.getInstance().getClientPackSource().downloadAndSelectResourcePack(realmsServerAddress.resourcePackUrl, realmsServerAddress.resourcePackHash).thenRun(() -> this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, realmsServerAddress2))))).exceptionally(function);
                                }
                                catch (Exception exception) {
                                    function.apply(exception);
                                }
                            } else {
                                GetServerDetailsTask.setScreen(this.lastScreen);
                            }
                        }
                        finally {
                            if (this.connectLock != null && this.connectLock.isHeldByCurrentThread()) {
                                this.connectLock.unlock();
                            }
                        }
                    }, RealmsLongConfirmationScreen.Type.Info, translatableComponent, translatableComponent2, true));
                } else {
                    this.setScreen(new RealmsLongRunningMcoTaskScreen(this.lastScreen, new ConnectTask(this.lastScreen, this.server, realmsServerAddress2)));
                }
            } else {
                this.error(new TranslatableComponent("mco.errorMessage.connectionFailure"));
            }
        }
    }

    private void sleep(int n) {
        try {
            Thread.sleep(n * 1000);
        }
        catch (InterruptedException interruptedException) {
            LOGGER.warn(interruptedException.getLocalizedMessage());
        }
    }
}

