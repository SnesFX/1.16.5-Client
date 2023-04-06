/*
 * Decompiled with CFR 0.146.
 * 
 * Could not load the following classes:
 *  com.google.common.collect.Iterables
 *  com.google.common.collect.Maps
 *  javax.annotation.Nullable
 */
package net.minecraft.world.item;

import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stat;
import net.minecraft.stats.StatType;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.AgableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.MobSpawnType;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.UseOnContext;
import net.minecraft.world.level.BaseSpawner;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.LiquidBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.SpawnerBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.VoxelShape;

public class SpawnEggItem
extends Item {
    private static final Map<EntityType<?>, SpawnEggItem> BY_ID = Maps.newIdentityHashMap();
    private final int color1;
    private final int color2;
    private final EntityType<?> defaultType;

    public SpawnEggItem(EntityType<?> entityType, int n, int n2, Item.Properties properties) {
        super(properties);
        this.defaultType = entityType;
        this.color1 = n;
        this.color2 = n2;
        BY_ID.put(entityType, this);
    }

    @Override
    public InteractionResult useOn(UseOnContext useOnContext) {
        Object object;
        Level level = useOnContext.getLevel();
        if (!(level instanceof ServerLevel)) {
            return InteractionResult.SUCCESS;
        }
        ItemStack itemStack = useOnContext.getItemInHand();
        BlockPos blockPos = useOnContext.getClickedPos();
        Direction direction = useOnContext.getClickedFace();
        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.is(Blocks.SPAWNER) && (object = level.getBlockEntity(blockPos)) instanceof SpawnerBlockEntity) {
            BaseSpawner baseSpawner = ((SpawnerBlockEntity)object).getSpawner();
            EntityType<?> entityType = this.getType(itemStack.getTag());
            baseSpawner.setEntityId(entityType);
            ((BlockEntity)object).setChanged();
            level.sendBlockUpdated(blockPos, blockState, blockState, 3);
            itemStack.shrink(1);
            return InteractionResult.CONSUME;
        }
        object = blockState.getCollisionShape(level, blockPos).isEmpty() ? blockPos : blockPos.relative(direction);
        EntityType<?> entityType = this.getType(itemStack.getTag());
        if (entityType.spawn((ServerLevel)level, itemStack, useOnContext.getPlayer(), (BlockPos)object, MobSpawnType.SPAWN_EGG, true, !Objects.equals(blockPos, object) && direction == Direction.UP) != null) {
            itemStack.shrink(1);
        }
        return InteractionResult.CONSUME;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        BlockHitResult blockHitResult = SpawnEggItem.getPlayerPOVHitResult(level, player, ClipContext.Fluid.SOURCE_ONLY);
        if (((HitResult)blockHitResult).getType() != HitResult.Type.BLOCK) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (!(level instanceof ServerLevel)) {
            return InteractionResultHolder.success(itemStack);
        }
        BlockHitResult blockHitResult2 = blockHitResult;
        BlockPos blockPos = blockHitResult2.getBlockPos();
        if (!(level.getBlockState(blockPos).getBlock() instanceof LiquidBlock)) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (!level.mayInteract(player, blockPos) || !player.mayUseItemAt(blockPos, blockHitResult2.getDirection(), itemStack)) {
            return InteractionResultHolder.fail(itemStack);
        }
        EntityType<?> entityType = this.getType(itemStack.getTag());
        if (entityType.spawn((ServerLevel)level, itemStack, player, blockPos, MobSpawnType.SPAWN_EGG, false, false) == null) {
            return InteractionResultHolder.pass(itemStack);
        }
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
        player.awardStat(Stats.ITEM_USED.get(this));
        return InteractionResultHolder.consume(itemStack);
    }

    public boolean spawnsEntity(@Nullable CompoundTag compoundTag, EntityType<?> entityType) {
        return Objects.equals(this.getType(compoundTag), entityType);
    }

    public int getColor(int n) {
        return n == 0 ? this.color1 : this.color2;
    }

    @Nullable
    public static SpawnEggItem byId(@Nullable EntityType<?> entityType) {
        return BY_ID.get(entityType);
    }

    public static Iterable<SpawnEggItem> eggs() {
        return Iterables.unmodifiableIterable(BY_ID.values());
    }

    public EntityType<?> getType(@Nullable CompoundTag compoundTag) {
        CompoundTag compoundTag2;
        if (compoundTag != null && compoundTag.contains("EntityTag", 10) && (compoundTag2 = compoundTag.getCompound("EntityTag")).contains("id", 8)) {
            return EntityType.byString(compoundTag2.getString("id")).orElse(this.defaultType);
        }
        return this.defaultType;
    }

    public Optional<Mob> spawnOffspringFromSpawnEgg(Player player, Mob mob, EntityType<? extends Mob> entityType, ServerLevel serverLevel, Vec3 vec3, ItemStack itemStack) {
        if (!this.spawnsEntity(itemStack.getTag(), entityType)) {
            return Optional.empty();
        }
        Mob mob2 = mob instanceof AgableMob ? ((AgableMob)mob).getBreedOffspring(serverLevel, (AgableMob)mob) : entityType.create(serverLevel);
        if (mob2 == null) {
            return Optional.empty();
        }
        mob2.setBaby(true);
        if (!mob2.isBaby()) {
            return Optional.empty();
        }
        mob2.moveTo(vec3.x(), vec3.y(), vec3.z(), 0.0f, 0.0f);
        serverLevel.addFreshEntityWithPassengers(mob2);
        if (itemStack.hasCustomHoverName()) {
            mob2.setCustomName(itemStack.getHoverName());
        }
        if (!player.abilities.instabuild) {
            itemStack.shrink(1);
        }
        return Optional.of(mob2);
    }
}

