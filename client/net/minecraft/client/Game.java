/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.Bridge
 *  com.mojang.bridge.game.GameSession
 *  com.mojang.bridge.game.GameVersion
 *  com.mojang.bridge.game.Language
 *  com.mojang.bridge.game.PerformanceMetrics
 *  com.mojang.bridge.game.RunningGame
 *  com.mojang.bridge.launcher.Launcher
 *  com.mojang.bridge.launcher.SessionEventListener
 *  javax.annotation.Nullable
 */
package net.minecraft.client;

import com.mojang.bridge.Bridge;
import com.mojang.bridge.game.GameSession;
import com.mojang.bridge.game.GameVersion;
import com.mojang.bridge.game.Language;
import com.mojang.bridge.game.PerformanceMetrics;
import com.mojang.bridge.game.RunningGame;
import com.mojang.bridge.launcher.Launcher;
import com.mojang.bridge.launcher.SessionEventListener;
import javax.annotation.Nullable;
import net.minecraft.SharedConstants;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Session;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.LanguageInfo;
import net.minecraft.client.resources.language.LanguageManager;
import net.minecraft.util.FrameTimer;

public class Game
implements RunningGame {
    private final Minecraft minecraft;
    @Nullable
    private final Launcher launcher;
    private SessionEventListener listener = SessionEventListener.NONE;

    public Game(Minecraft minecraft) {
        this.minecraft = minecraft;
        this.launcher = Bridge.getLauncher();
        if (this.launcher != null) {
            this.launcher.registerGame((RunningGame)this);
        }
    }

    public GameVersion getVersion() {
        return SharedConstants.getCurrentVersion();
    }

    public Language getSelectedLanguage() {
        return this.minecraft.getLanguageManager().getSelected();
    }

    @Nullable
    public GameSession getCurrentSession() {
        ClientLevel clientLevel = this.minecraft.level;
        return clientLevel == null ? null : new Session(clientLevel, this.minecraft.player, this.minecraft.player.connection);
    }

    public PerformanceMetrics getPerformanceMetrics() {
        FrameTimer frameTimer = this.minecraft.getFrameTimer();
        long l = Integer.MAX_VALUE;
        long l2 = Integer.MIN_VALUE;
        long l3 = 0L;
        for (long l4 : frameTimer.getLog()) {
            l = Math.min(l, l4);
            l2 = Math.max(l2, l4);
            l3 += l4;
        }
        return new Metrics((int)l, (int)l2, (int)(l3 / (long)frameTimer.getLog().length), frameTimer.getLog().length);
    }

    public void setSessionEventListener(SessionEventListener sessionEventListener) {
        this.listener = sessionEventListener;
    }

    public void onStartGameSession() {
        this.listener.onStartGameSession(this.getCurrentSession());
    }

    public void onLeaveGameSession() {
        this.listener.onLeaveGameSession(this.getCurrentSession());
    }

    static class Metrics
    implements PerformanceMetrics {
        private final int min;
        private final int max;
        private final int average;
        private final int samples;

        public Metrics(int n, int n2, int n3, int n4) {
            this.min = n;
            this.max = n2;
            this.average = n3;
            this.samples = n4;
        }

        public int getMinTime() {
            return this.min;
        }

        public int getMaxTime() {
            return this.max;
        }

        public int getAverageTime() {
            return this.average;
        }

        public int getSampleCount() {
            return this.samples;
        }
    }

}

