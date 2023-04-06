/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.BiMap
 *  com.google.common.collect.HashBiMap
 *  javax.annotation.Nullable
 */
package net.minecraft.world.level.storage.loot.parameters;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import java.util.function.Consumer;
import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.storage.loot.parameters.LootContextParam;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSet;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.phys.Vec3;

public class LootContextParamSets {
    private static final BiMap<ResourceLocation, LootContextParamSet> REGISTRY = HashBiMap.create();
    public static final LootContextParamSet EMPTY = LootContextParamSets.register("empty", builder -> {});
    public static final LootContextParamSet CHEST = LootContextParamSets.register("chest", builder -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
    public static final LootContextParamSet COMMAND = LootContextParamSets.register("command", builder -> builder.required(LootContextParams.ORIGIN).optional(LootContextParams.THIS_ENTITY));
    public static final LootContextParamSet SELECTOR = LootContextParamSets.register("selector", builder -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
    public static final LootContextParamSet FISHING = LootContextParamSets.register("fishing", builder -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY));
    public static final LootContextParamSet ENTITY = LootContextParamSets.register("entity", builder -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.DAMAGE_SOURCE).optional(LootContextParams.KILLER_ENTITY).optional(LootContextParams.DIRECT_KILLER_ENTITY).optional(LootContextParams.LAST_DAMAGE_PLAYER));
    public static final LootContextParamSet GIFT = LootContextParamSets.register("gift", builder -> builder.required(LootContextParams.ORIGIN).required(LootContextParams.THIS_ENTITY));
    public static final LootContextParamSet PIGLIN_BARTER = LootContextParamSets.register("barter", builder -> builder.required(LootContextParams.THIS_ENTITY));
    public static final LootContextParamSet ADVANCEMENT_REWARD = LootContextParamSets.register("advancement_reward", builder -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN));
    public static final LootContextParamSet ADVANCEMENT_ENTITY = LootContextParamSets.register("advancement_entity", builder -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.ORIGIN));
    public static final LootContextParamSet ALL_PARAMS = LootContextParamSets.register("generic", builder -> builder.required(LootContextParams.THIS_ENTITY).required(LootContextParams.LAST_DAMAGE_PLAYER).required(LootContextParams.DAMAGE_SOURCE).required(LootContextParams.KILLER_ENTITY).required(LootContextParams.DIRECT_KILLER_ENTITY).required(LootContextParams.ORIGIN).required(LootContextParams.BLOCK_STATE).required(LootContextParams.BLOCK_ENTITY).required(LootContextParams.TOOL).required(LootContextParams.EXPLOSION_RADIUS));
    public static final LootContextParamSet BLOCK = LootContextParamSets.register("block", builder -> builder.required(LootContextParams.BLOCK_STATE).required(LootContextParams.ORIGIN).required(LootContextParams.TOOL).optional(LootContextParams.THIS_ENTITY).optional(LootContextParams.BLOCK_ENTITY).optional(LootContextParams.EXPLOSION_RADIUS));

    private static LootContextParamSet register(String string, Consumer<LootContextParamSet.Builder> consumer) {
        LootContextParamSet.Builder builder = new LootContextParamSet.Builder();
        consumer.accept(builder);
        LootContextParamSet lootContextParamSet = builder.build();
        ResourceLocation resourceLocation = new ResourceLocation(string);
        LootContextParamSet lootContextParamSet2 = (LootContextParamSet)REGISTRY.put((Object)resourceLocation, (Object)lootContextParamSet);
        if (lootContextParamSet2 != null) {
            throw new IllegalStateException("Loot table parameter set " + resourceLocation + " is already registered");
        }
        return lootContextParamSet;
    }

    @Nullable
    public static LootContextParamSet get(ResourceLocation resourceLocation) {
        return (LootContextParamSet)REGISTRY.get((Object)resourceLocation);
    }

    @Nullable
    public static ResourceLocation getKey(LootContextParamSet lootContextParamSet) {
        return (ResourceLocation)REGISTRY.inverse().get((Object)lootContextParamSet);
    }
}

