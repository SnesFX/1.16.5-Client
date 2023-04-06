/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.level;

import java.util.function.Consumer;
import java.util.stream.Stream;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelWriter;

public interface ServerLevelAccessor
extends LevelAccessor {
    public ServerLevel getLevel();

    default public void addFreshEntityWithPassengers(Entity entity) {
        entity.getSelfAndPassengers().forEach(this::addFreshEntity);
    }
}

