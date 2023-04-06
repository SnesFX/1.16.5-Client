/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 */
package net.minecraft.gametest.framework;

import com.google.common.collect.Lists;
import java.util.Collection;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.gametest.framework.GameTestInfo;

public class GameTestTicker {
    public static final GameTestTicker singleton = new GameTestTicker();
    private final Collection<GameTestInfo> testInfos = Lists.newCopyOnWriteArrayList();

    public void add(GameTestInfo gameTestInfo) {
        this.testInfos.add(gameTestInfo);
    }

    public void clear() {
        this.testInfos.clear();
    }

    public void tick() {
        this.testInfos.forEach(GameTestInfo::tick);
        this.testInfos.removeIf(GameTestInfo::isDone);
    }
}

