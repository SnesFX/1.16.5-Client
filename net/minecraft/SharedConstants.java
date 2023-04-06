/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.mojang.bridge.game.GameVersion
 *  com.mojang.brigadier.exceptions.BuiltInExceptionProvider
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  io.netty.util.ResourceLeakDetector
 *  io.netty.util.ResourceLeakDetector$Level
 */
package net.minecraft;

import com.mojang.bridge.game.GameVersion;
import com.mojang.brigadier.exceptions.BuiltInExceptionProvider;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import io.netty.util.ResourceLeakDetector;
import java.time.Duration;
import net.minecraft.DetectedVersion;
import net.minecraft.commands.BrigadierExceptions;

public class SharedConstants {
    public static final ResourceLeakDetector.Level NETTY_LEAK_DETECTION = ResourceLeakDetector.Level.DISABLED;
    public static final long MAXIMUM_TICK_TIME_NANOS = Duration.ofMillis(300L).toNanos();
    public static boolean CHECK_DATA_FIXER_SCHEMA = true;
    public static boolean IS_RUNNING_IN_IDE;
    public static final char[] ILLEGAL_FILE_CHARACTERS;
    private static GameVersion CURRENT_VERSION;

    public static boolean isAllowedChatCharacter(char c) {
        return c != '\u00a7' && c >= ' ' && c != '\u007f';
    }

    public static String filterText(String string) {
        StringBuilder stringBuilder = new StringBuilder();
        for (char c : string.toCharArray()) {
            if (!SharedConstants.isAllowedChatCharacter(c)) continue;
            stringBuilder.append(c);
        }
        return stringBuilder.toString();
    }

    public static GameVersion getCurrentVersion() {
        if (CURRENT_VERSION == null) {
            CURRENT_VERSION = DetectedVersion.tryDetectVersion();
        }
        return CURRENT_VERSION;
    }

    public static int getProtocolVersion() {
        return 754;
    }

    static {
        ILLEGAL_FILE_CHARACTERS = new char[]{'/', '\n', '\r', '\t', '\u0000', '\f', '`', '?', '*', '\\', '<', '>', '|', '\"', ':'};
        ResourceLeakDetector.setLevel((ResourceLeakDetector.Level)NETTY_LEAK_DETECTION);
        CommandSyntaxException.ENABLE_COMMAND_STACK_TRACES = false;
        CommandSyntaxException.BUILT_IN_EXCEPTIONS = new BrigadierExceptions();
    }
}

