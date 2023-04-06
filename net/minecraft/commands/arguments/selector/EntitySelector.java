/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Lists
 *  com.mojang.brigadier.exceptions.CommandSyntaxException
 *  com.mojang.brigadier.exceptions.SimpleCommandExceptionType
 *  javax.annotation.Nullable
 */
package net.minecraft.commands.arguments.selector;

import com.google.common.collect.Lists;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Predicate;
import javax.annotation.Nullable;
import net.minecraft.advancements.critereon.MinMaxBounds;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.EntityArgument;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;

public class EntitySelector {
    private final int maxResults;
    private final boolean includesEntities;
    private final boolean worldLimited;
    private final Predicate<Entity> predicate;
    private final MinMaxBounds.Floats range;
    private final Function<Vec3, Vec3> position;
    @Nullable
    private final AABB aabb;
    private final BiConsumer<Vec3, List<? extends Entity>> order;
    private final boolean currentEntity;
    @Nullable
    private final String playerName;
    @Nullable
    private final UUID entityUUID;
    @Nullable
    private final EntityType<?> type;
    private final boolean usesSelector;

    public EntitySelector(int n, boolean bl, boolean bl2, Predicate<Entity> predicate, MinMaxBounds.Floats floats, Function<Vec3, Vec3> function, @Nullable AABB aABB, BiConsumer<Vec3, List<? extends Entity>> biConsumer, boolean bl3, @Nullable String string, @Nullable UUID uUID, @Nullable EntityType<?> entityType, boolean bl4) {
        this.maxResults = n;
        this.includesEntities = bl;
        this.worldLimited = bl2;
        this.predicate = predicate;
        this.range = floats;
        this.position = function;
        this.aabb = aABB;
        this.order = biConsumer;
        this.currentEntity = bl3;
        this.playerName = string;
        this.entityUUID = uUID;
        this.type = entityType;
        this.usesSelector = bl4;
    }

    public int getMaxResults() {
        return this.maxResults;
    }

    public boolean includesEntities() {
        return this.includesEntities;
    }

    public boolean isSelfSelector() {
        return this.currentEntity;
    }

    public boolean isWorldLimited() {
        return this.worldLimited;
    }

    private void checkPermissions(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        if (this.usesSelector && !commandSourceStack.hasPermission(2)) {
            throw EntityArgument.ERROR_SELECTORS_NOT_ALLOWED.create();
        }
    }

    public Entity findSingleEntity(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        this.checkPermissions(commandSourceStack);
        List<? extends Entity> list = this.findEntities(commandSourceStack);
        if (list.isEmpty()) {
            throw EntityArgument.NO_ENTITIES_FOUND.create();
        }
        if (list.size() > 1) {
            throw EntityArgument.ERROR_NOT_SINGLE_ENTITY.create();
        }
        return list.get(0);
    }

    public List<? extends Entity> findEntities(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        this.checkPermissions(commandSourceStack);
        if (!this.includesEntities) {
            return this.findPlayers(commandSourceStack);
        }
        if (this.playerName != null) {
            ServerPlayer serverPlayer = commandSourceStack.getServer().getPlayerList().getPlayerByName(this.playerName);
            if (serverPlayer == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[]{serverPlayer});
        }
        if (this.entityUUID != null) {
            for (ServerLevel serverLevel : commandSourceStack.getServer().getAllLevels()) {
                Entity entity = serverLevel.getEntity(this.entityUUID);
                if (entity == null) continue;
                return Lists.newArrayList((Object[])new Entity[]{entity});
            }
            return Collections.emptyList();
        }
        Vec3 vec3 = this.position.apply(commandSourceStack.getPosition());
        Predicate<Entity> predicate = this.getPredicate(vec3);
        if (this.currentEntity) {
            if (commandSourceStack.getEntity() != null && predicate.test(commandSourceStack.getEntity())) {
                return Lists.newArrayList((Object[])new Entity[]{commandSourceStack.getEntity()});
            }
            return Collections.emptyList();
        }
        ArrayList arrayList = Lists.newArrayList();
        if (this.isWorldLimited()) {
            this.addEntities(arrayList, commandSourceStack.getLevel(), vec3, predicate);
        } else {
            for (ServerLevel serverLevel : commandSourceStack.getServer().getAllLevels()) {
                this.addEntities(arrayList, serverLevel, vec3, predicate);
            }
        }
        return this.sortAndLimit(vec3, arrayList);
    }

    private void addEntities(List<Entity> list, ServerLevel serverLevel, Vec3 vec3, Predicate<Entity> predicate) {
        if (this.aabb != null) {
            list.addAll(serverLevel.getEntities(this.type, this.aabb.move(vec3), predicate));
        } else {
            list.addAll(serverLevel.getEntities(this.type, predicate));
        }
    }

    public ServerPlayer findSinglePlayer(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        this.checkPermissions(commandSourceStack);
        List<ServerPlayer> list = this.findPlayers(commandSourceStack);
        if (list.size() != 1) {
            throw EntityArgument.NO_PLAYERS_FOUND.create();
        }
        return list.get(0);
    }

    public List<ServerPlayer> findPlayers(CommandSourceStack commandSourceStack) throws CommandSyntaxException {
        List<Object> list;
        this.checkPermissions(commandSourceStack);
        if (this.playerName != null) {
            ServerPlayer serverPlayer = commandSourceStack.getServer().getPlayerList().getPlayerByName(this.playerName);
            if (serverPlayer == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[]{serverPlayer});
        }
        if (this.entityUUID != null) {
            ServerPlayer serverPlayer = commandSourceStack.getServer().getPlayerList().getPlayer(this.entityUUID);
            if (serverPlayer == null) {
                return Collections.emptyList();
            }
            return Lists.newArrayList((Object[])new ServerPlayer[]{serverPlayer});
        }
        Vec3 vec3 = this.position.apply(commandSourceStack.getPosition());
        Predicate<Entity> predicate = this.getPredicate(vec3);
        if (this.currentEntity) {
            ServerPlayer serverPlayer;
            if (commandSourceStack.getEntity() instanceof ServerPlayer && predicate.test(serverPlayer = (ServerPlayer)commandSourceStack.getEntity())) {
                return Lists.newArrayList((Object[])new ServerPlayer[]{serverPlayer});
            }
            return Collections.emptyList();
        }
        if (this.isWorldLimited()) {
            list = commandSourceStack.getLevel().getPlayers(predicate::test);
        } else {
            list = Lists.newArrayList();
            for (ServerPlayer serverPlayer : commandSourceStack.getServer().getPlayerList().getPlayers()) {
                if (!predicate.test(serverPlayer)) continue;
                list.add(serverPlayer);
            }
        }
        return this.sortAndLimit(vec3, list);
    }

    private Predicate<Entity> getPredicate(Vec3 vec3) {
        Predicate<Entity> predicate = this.predicate;
        if (this.aabb != null) {
            AABB aABB = this.aabb.move(vec3);
            predicate = predicate.and(entity -> aABB.intersects(entity.getBoundingBox()));
        }
        if (!this.range.isAny()) {
            predicate = predicate.and(entity -> this.range.matchesSqr(entity.distanceToSqr(vec3)));
        }
        return predicate;
    }

    private <T extends Entity> List<T> sortAndLimit(Vec3 vec3, List<T> list) {
        if (list.size() > 1) {
            this.order.accept(vec3, list);
        }
        return list.subList(0, Math.min(this.maxResults, list.size()));
    }

    public static MutableComponent joinNames(List<? extends Entity> list) {
        return ComponentUtils.formatList(list, Entity::getDisplayName);
    }
}

