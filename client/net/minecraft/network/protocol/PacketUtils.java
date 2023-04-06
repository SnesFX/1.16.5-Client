/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  org.apache.logging.log4j.LogManager
 *  org.apache.logging.log4j.Logger
 */
package net.minecraft.network.protocol;

import net.minecraft.network.Connection;
import net.minecraft.network.PacketListener;
import net.minecraft.network.protocol.Packet;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.RunningOnDifferentThreadException;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.thread.BlockableEventLoop;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class PacketUtils {
    private static final Logger LOGGER = LogManager.getLogger();

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T t, ServerLevel serverLevel) throws RunningOnDifferentThreadException {
        PacketUtils.ensureRunningOnSameThread(packet, t, serverLevel.getServer());
    }

    public static <T extends PacketListener> void ensureRunningOnSameThread(Packet<T> packet, T t, BlockableEventLoop<?> blockableEventLoop) throws RunningOnDifferentThreadException {
        if (!blockableEventLoop.isSameThread()) {
            blockableEventLoop.execute(() -> {
                if (t.getConnection().isConnected()) {
                    packet.handle(t);
                } else {
                    LOGGER.debug("Ignoring packet due to disconnection: " + packet);
                }
            });
            throw RunningOnDifferentThreadException.RUNNING_ON_DIFFERENT_THREAD;
        }
    }
}

