/*
 * Decompiled with CFR 0.146.
 */
package net.minecraft.world.entity.vehicle;

import java.util.Random;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.util.Mth;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Abilities;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.FurnaceBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.phys.Vec3;

public class MinecartFurnace
extends AbstractMinecart {
    private static final EntityDataAccessor<Boolean> DATA_ID_FUEL = SynchedEntityData.defineId(MinecartFurnace.class, EntityDataSerializers.BOOLEAN);
    private int fuel;
    public double xPush;
    public double zPush;
    private static final Ingredient INGREDIENT = Ingredient.of(Items.COAL, Items.CHARCOAL);

    public MinecartFurnace(EntityType<? extends MinecartFurnace> entityType, Level level) {
        super(entityType, level);
    }

    public MinecartFurnace(Level level, double d, double d2, double d3) {
        super(EntityType.FURNACE_MINECART, level, d, d2, d3);
    }

    @Override
    public AbstractMinecart.Type getMinecartType() {
        return AbstractMinecart.Type.FURNACE;
    }

    @Override
    protected void defineSynchedData() {
        super.defineSynchedData();
        this.entityData.define(DATA_ID_FUEL, false);
    }

    @Override
    public void tick() {
        super.tick();
        if (!this.level.isClientSide()) {
            if (this.fuel > 0) {
                --this.fuel;
            }
            if (this.fuel <= 0) {
                this.xPush = 0.0;
                this.zPush = 0.0;
            }
            this.setHasFuel(this.fuel > 0);
        }
        if (this.hasFuel() && this.random.nextInt(4) == 0) {
            this.level.addParticle(ParticleTypes.LARGE_SMOKE, this.getX(), this.getY() + 0.8, this.getZ(), 0.0, 0.0, 0.0);
        }
    }

    @Override
    protected double getMaxSpeed() {
        return 0.2;
    }

    @Override
    public void destroy(DamageSource damageSource) {
        super.destroy(damageSource);
        if (!damageSource.isExplosion() && this.level.getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.spawnAtLocation(Blocks.FURNACE);
        }
    }

    @Override
    protected void moveAlongTrack(BlockPos blockPos, BlockState blockState) {
        double d = 1.0E-4;
        double d2 = 0.001;
        super.moveAlongTrack(blockPos, blockState);
        Vec3 vec3 = this.getDeltaMovement();
        double d3 = MinecartFurnace.getHorizontalDistanceSqr(vec3);
        double d4 = this.xPush * this.xPush + this.zPush * this.zPush;
        if (d4 > 1.0E-4 && d3 > 0.001) {
            double d5 = Mth.sqrt(d3);
            double d6 = Mth.sqrt(d4);
            this.xPush = vec3.x / d5 * d6;
            this.zPush = vec3.z / d5 * d6;
        }
    }

    @Override
    protected void applyNaturalSlowdown() {
        double d = this.xPush * this.xPush + this.zPush * this.zPush;
        if (d > 1.0E-7) {
            d = Mth.sqrt(d);
            this.xPush /= d;
            this.zPush /= d;
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.8, 0.0, 0.8).add(this.xPush, 0.0, this.zPush));
        } else {
            this.setDeltaMovement(this.getDeltaMovement().multiply(0.98, 0.0, 0.98));
        }
        super.applyNaturalSlowdown();
    }

    @Override
    public InteractionResult interact(Player player, InteractionHand interactionHand) {
        ItemStack itemStack = player.getItemInHand(interactionHand);
        if (INGREDIENT.test(itemStack) && this.fuel + 3600 <= 32000) {
            if (!player.abilities.instabuild) {
                itemStack.shrink(1);
            }
            this.fuel += 3600;
        }
        if (this.fuel > 0) {
            this.xPush = this.getX() - player.getX();
            this.zPush = this.getZ() - player.getZ();
        }
        return InteractionResult.sidedSuccess(this.level.isClientSide);
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        super.addAdditionalSaveData(compoundTag);
        compoundTag.putDouble("PushX", this.xPush);
        compoundTag.putDouble("PushZ", this.zPush);
        compoundTag.putShort("Fuel", (short)this.fuel);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        super.readAdditionalSaveData(compoundTag);
        this.xPush = compoundTag.getDouble("PushX");
        this.zPush = compoundTag.getDouble("PushZ");
        this.fuel = compoundTag.getShort("Fuel");
    }

    protected boolean hasFuel() {
        return this.entityData.get(DATA_ID_FUEL);
    }

    protected void setHasFuel(boolean bl) {
        this.entityData.set(DATA_ID_FUEL, bl);
    }

    @Override
    public BlockState getDefaultDisplayBlockState() {
        return (BlockState)((BlockState)Blocks.FURNACE.defaultBlockState().setValue(FurnaceBlock.FACING, Direction.NORTH)).setValue(FurnaceBlock.LIT, this.hasFuel());
    }
}

